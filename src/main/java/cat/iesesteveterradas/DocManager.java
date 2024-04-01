package cat.iesesteveterradas;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.xml.sax.InputSource;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.apache.commons.text.StringEscapeUtils;

public class DocManager {
    
    public void insertMongo(String url, String dbName, String collectionName, MongoDoc doc) {
        //Conectarse
        try (var mongoClient = MongoClients.create(url)) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = db.getCollection(collectionName);

            //Insert
            collection.insertOne(doc.getDocument());
            PR32CreateMain.logger.info("Inserted Correctly");
            System.out.println("Inserted Correctly");
        } catch (Exception e) {
            PR32CreateMain.logger.error("An error ocurred: " + e.getMessage());
            System.out.println("An error ocurred: " + e.getMessage());
        }
    }

    public MongoDoc xmlToMongoDoc(String url) {     //Llegir dades
        MongoDoc resultado;
        //Vamo a leer 
        try {
            org.w3c.dom.Document doc = getDocument(url);
            //Obtindre dades
            String id = doc.getElementsByTagName("Id").item(0).getTextContent();   
            String postTpeId = doc.getElementsByTagName("PostTypeId").item(0).getTextContent();   
            String acceptedAnswerId = doc.getElementsByTagName("AcceptedAnswerId").item(0).getTextContent();   
            String cDate = doc.getElementsByTagName("CreationDate").item(0).getTextContent();   
            String score = doc.getElementsByTagName("Score").item(0).getTextContent();   
            String viewCount = doc.getElementsByTagName("ViewCount").item(0).getTextContent();   
            String body = StringEscapeUtils.unescapeHtml4(doc.getElementsByTagName("Body").item(0).getTextContent());   //Aqui modifiquem el String
            String ownerUserId = doc.getElementsByTagName("OwnerUserId").item(0).getTextContent();   
            String lastDate = doc.getElementsByTagName("LastActivityDate").item(0).getTextContent();   
            String title = doc.getElementsByTagName("Title").item(0).getTextContent();   
            String tags = doc.getElementsByTagName("Tags").item(0).getTextContent();   
            String ansCount = doc.getElementsByTagName("AnswerCount").item(0).getTextContent();   
            String comCount = doc.getElementsByTagName("CommentCount").item(0).getTextContent();   
            String license = doc.getElementsByTagName("ContentLicense").item(0).getTextContent();   
            
            resultado = new MongoDoc(postTpeId, id, acceptedAnswerId, cDate, score, viewCount, body, ownerUserId, lastDate, title, tags, ansCount, comCount, license);

            PR32CreateMain.logger.info("Read Correctly");
            System.out.println("Read Correctly");
        } catch (Exception e) {
            PR32CreateMain.logger.error("An error ocurred: " + e.getMessage());
            System.out.println("An error ocurred: " + e.getMessage());
            return null;
        }
        return resultado;
    }

    org.w3c.dom.Document getDocument(String url) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document result = builder.parse(new InputSource(url));
            return result;
        } catch (Exception e) {
            PR32CreateMain.logger.error("An error ocurred: " + e.getMessage());
            System.out.println("An error ocurred: " + e.getMessage());
        }
        return null;
    }
}
