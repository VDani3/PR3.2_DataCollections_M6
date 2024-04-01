package cat.iesesteveterradas;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import java.util.ArrayList;
import java.util.Arrays;

public class PR32QueryMain {
    
    public static void main(String[] args) {
        String dbUrl = "mongodb://root:example@localhost:27017";
        String dbName = "japanese";
        String collectionName = "collection";

        //agafem el avg
        try (var mongoClient = MongoClients.create(dbUrl)){
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = db.getCollection(collectionName);

            //Aquet aggregateIterable m'ho ha ensenyat ChatGPT:
            AggregateIterable<Document> averageResult = collection.aggregate(
                    Arrays.asList(
                            new Document("$group", new Document("_id", null)
                                    .append("avgViewCount", new Document("$avg", "$ViewCount")))
                    )
            );

            double avgViewCount = 0;
            for (Document doc : averageResult) {
                avgViewCount = doc.getDouble("avgViewCount");
            }

            //Obtenim els doc que tenen mes viewCount de la mitjana
            Document query = new Document("ViewCount", new Document("$gt", avgViewCount));
            FindIterable<Document> res = collection.find(query);

            //Iterem i guardem els resultats
            System.out.println("Documents with higher ViewCount than Average ");
            System.out.println("-----------------------------------------------");
            for (Document doc : res) {
                System.out.println("  - : "+doc.getString("Title"));
                ///fer lo del pdf
            }
            System.out.println("-----------------------------------------------\n\n");

            //Obtenir els docs que tenen les seguents tres lletres
            ArrayList<String> lletres = new ArrayList<>(Arrays.asList("pug", "wig", "yak", "nap", "jig", "mug", "zap", "gag", "oaf", "elf"));

            System.out.println("Documents with higher ViewCount than Average ");
            System.out.println("-----------------------------------------------");
            //Iterem entre el conjunt de lletres
            for (String srt : lletres) {
                Document q2 = new Document("Title", new Document("$regex", srt).append("$options", "i"));
                FindIterable<Document> r = collection.find(q2);

                //Iterem en els resultats
                for (Document dc : r) {
                    System.out.println("Document: "+dc.getString("Title"));
                    ///Pdf
                }
            }
            System.out.println("-----------------------------------------------");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
