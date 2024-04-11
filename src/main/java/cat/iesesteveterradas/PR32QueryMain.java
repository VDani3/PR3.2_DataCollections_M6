package cat.iesesteveterradas;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PR32QueryMain {
    
    public static void main(String[] args) {
        String dbUrl = "mongodb://root:example@localhost:27017";
        String dbName = "japanese";
        String collectionName = "collection";
        List<String> pdfLines = new ArrayList<>();

        //agafem el avg
        try (var mongoClient = MongoClients.create(dbUrl)){
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = db.getCollection(collectionName);

            
            // Pas 1: Utilitzem l'operació d'agregació per a calcular la mitjana de viewCount en tots els documents.
            // Creem el pipeline d'agregació necessari.
            // Aquesta operació consisteix en un sol pas (stage) que agrupa tots els documents (el valor null en '_id' indica agrupació global)
            // i calcula la mitjana de 'viewCount' utilitzant l'operador $avg.
            AggregateIterable<Document> aggregateResult = collection.aggregate(
                Arrays.asList(
                        new Document("$group", new Document("_id", null).append("avgViewCount", new Document("$avg", "$viewCount")))
                )
            );

            double avgViewCount = 0; // Inicialitzem la variable que emmagatzemarà la mitjana de viewCount

            // Iterem sobre el resultat de l'agregació.
            // Com que esperem un sol resultat (la mitjana global), només hauríem de tenir un document en el resultat.
            for (Document doc : aggregateResult) {
                avgViewCount = doc.getDouble("avgViewCount"); // Assignem el valor calculat a la variable avgViewCount
            }

            // Pas 2: Ara que tenim la mitjana, construïm una consulta per trobar els documents on viewCount és major que la mitjana.
            Bson query = new Document("viewCount", new Document("$gt", avgViewCount));
            FindIterable<Document> res = collection.find(query); // Executem la consulta amb el criteri definit anteriorment.

            // Pas 3: Iterem sobre els resultats obtinguts i els imprimim.
            // Aquests documents tenen un viewCount superior a la mitjana calculada en el pas 1.
            System.out.println("Documents with higher ViewCount than Average ");
            System.out.println("-----------------------------------------------");
            for (Document doc : res) {
                System.out.println(doc.toJson()); // Imprimim cada document en format JSON
                pdfLines.add(doc.get("title").toString());
                pdfLines.add(doc.get("body").toString());
                // Aquí podríem fer altres operacions, com generar un PDF amb els resultats, si es desitja.
            }
            System.out.println("-----------------------------------------------\n\n");
            PdfMaker.generatePdf(pdfLines, System.getProperty("user.dir")+"/data/out/informe2.pdf");
            pdfLines.clear();


            
            //Obtenir els docs que tenen les seguents tres lletres
            ArrayList<String> lletres = new ArrayList<>(Arrays.asList("pug", "wig", "yak", "nap", "jig", "mug", "zap", "gag", "oaf", "elf", "Do", "both"));

            System.out.println("Documents with any of these 3 words ");
            System.out.println("-----------------------------------------------");
            //Iterem entre el conjunt de lletres
            for (String srt : lletres) {
                Bson q2 = new Document("title", new Document("$regex", ".*"+srt+".*").append("$options", "i"));
                FindIterable<Document> r = collection.find(q2);

                //Iterem en els resultats
                for (Document dc : r) {
                    System.out.println(dc.get("title").toString());
                    pdfLines.add(dc.get("title").toString());
                    pdfLines.add(dc.get("body").toString());
                }
            }
            System.out.println(System.getProperty("user.dir")+"/data/out/informe2.pdf");
            PdfMaker.generatePdf(pdfLines, System.getProperty("user.dir")+"/data/out/informe2.pdf");
            System.out.println("-----------------------------------------------"); 

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
