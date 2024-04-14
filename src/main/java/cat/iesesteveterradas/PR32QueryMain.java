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
import java.util.logging.Logger;

public class PR32QueryMain {
    public static final Logger logger = Logger.getLogger(PR32QueryMain.class.getName());

    public static void main(String[] args) {

        DocManager.setupLogger();

        String dbUrl = "mongodb://root:example@localhost:27017";
        String dbName = "japanese";
        String collectionName = "collection";
        List<String> pdfLines = new ArrayList<>();

        //agafem el avg
        try (var mongoClient = MongoClients.create(dbUrl)){
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = db.getCollection(collectionName);

            
            // Calcular la mitjana
            AggregateIterable<Document> aggregateResult = collection.aggregate(
                Arrays.asList(
                        new Document("$group", new Document("_id", null).append("avgViewCount", new Document("$avg", "$viewCount")))
                )
            );

            double avgViewCount = 0; // Inicialitzem la variable que emmagatzemarà la mitjana de viewCount

            for (Document doc : aggregateResult) {
                avgViewCount = doc.getDouble("avgViewCount"); // Assignem el valor calculat a la variable avgViewCount
            }

            // Construïm una consulta per trobar els documents on viewCount és major que la mitjana.
            Bson query = new Document("viewCount", new Document("$gt", avgViewCount));
            FindIterable<Document> res = collection.find(query); // Executem la consulta amb el criteri definit anteriorment.

            // Pas 3: Iterem sobre els resultats obtinguts i els imprimim.
            // Aquests documents tenen un viewCount superior a la mitjana calculada en el pas 1.
            System.out.println("\n\nDocuments with higher ViewCount than Average ");
            System.out.println("-----------------------------------------------");
            for (Document doc : res) {
                System.out.println(doc.get("id").toString() + ". "+doc.get("title").toString()); // Imprimim cada document en format JSON
                pdfLines.add("ID: " + doc.getInteger("id"));
                pdfLines.add("Post Type ID: " + doc.getString("postTypeId"));
                pdfLines.add("Accepted Answer ID: " + doc.getString("acceptedAnswerId"));
                pdfLines.add("Creation Date: " + doc.getString("creationDate"));
                pdfLines.add("Score: " + doc.getInteger("score"));
                pdfLines.add("View Count: " + doc.getInteger("viewCount"));
                pdfLines.add("Body: " + doc.getString("body").replaceAll("<[^>]*>", "")); // Remove HTML tags
                pdfLines.add("Owner User ID: " + doc.getString("ownerUserId"));
                pdfLines.add("Last Activity Date: " + doc.getString("lastActivityDate"));
                pdfLines.add("Title: " + doc.getString("title"));
                pdfLines.add("Tags: " + doc.getString("tags").replaceAll("<[^>]*>", ", ").replaceAll(", $", ""));
                pdfLines.add("Answer Count: " + doc.getString("answerCount"));
                pdfLines.add("Comment Count: " + doc.getString("commentCount"));
                pdfLines.add("Content License: " + doc.getString("contentLicense"));
                pdfLines.add("\f"); 
                // Aquí podríem fer altres operacions, com generar un PDF amb els resultats, si es desitja.
            }
            System.out.println("-----------------------------------------------\n\n");
            logger.info("Average showed and PDF created");
            PdfMaker.generatePdf(pdfLines, System.getProperty("user.dir")+"/data/out/informe.pdf");
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
                    System.out.println(dc.get("id").toString() + ". "+dc.get("title").toString());
                    pdfLines.add("ID: " + dc.getInteger("id"));
                    pdfLines.add("Post Type ID: " + dc.getString("postTypeId"));
                    pdfLines.add("Accepted Answer ID: " + dc.getString("acceptedAnswerId"));
                    pdfLines.add("Creation Date: " + dc.getString("creationDate"));
                    pdfLines.add("Score: " + dc.getInteger("score"));
                    pdfLines.add("View Count: " + dc.getInteger("viewCount"));
                    pdfLines.add("Body: " + dc.getString("body").replaceAll("<[^>]*>", "")); // Remove HTML tags
                    pdfLines.add("Owner User ID: " + dc.getString("ownerUserId"));
                    pdfLines.add("Last Activity Date: " + dc.getString("lastActivityDate"));
                    pdfLines.add("Title: " + dc.getString("title"));
                    pdfLines.add("Tags: " + dc.getString("tags").replaceAll("<[^>]*>", ", ").replaceAll(", $", ""));
                    pdfLines.add("Answer Count: " + dc.getString("answerCount"));
                    pdfLines.add("Comment Count: " + dc.getString("commentCount"));
                    pdfLines.add("Content License: " + dc.getString("contentLicense"));
                    pdfLines.add("\f");
                }
            }
            
            PdfMaker.generatePdf(pdfLines, System.getProperty("user.dir")+"/data/out/informe2.pdf");
            System.out.println("-----------------------------------------------"); 
            logger.info("Most viewed showed and PDF created");
            
        } catch (Exception e) {
            logger.severe("An error has ocurred: "+e.getMessage());
            e.printStackTrace();
        }
    }
}
