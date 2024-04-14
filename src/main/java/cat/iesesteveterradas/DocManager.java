package cat.iesesteveterradas;


import java.io.IOException;
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.xml.sax.InputSource;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.commons.text.StringEscapeUtils;

public class DocManager {

    public static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler(System.getProperty("user.dir")+"/data/logs/PR32Query.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            PR32QueryMain.logger.setLevel(Level.INFO);
            PR32QueryMain.logger.addHandler(fileHandler);

            FileHandler fileHandler2 = new FileHandler(System.getProperty("user.dir")+"/data/logs/PR32CreateMain.log", true);
            fileHandler2.setFormatter(new SimpleFormatter());
            PR32CreateMain.logger.setLevel(Level.INFO);
            PR32CreateMain.logger.addHandler(fileHandler2);

        } catch (SecurityException e) {
            System.out.println("Security error");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void insertMongo(String url, String dbName, String collectionName, List<MongoDoc> doc) {
        //Conectarse
        try (var mongoClient = MongoClients.create(url)) {
            MongoDatabase db = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = db.getCollection(collectionName);

            //Insert
            for (MongoDoc mongoDoc : doc) {
                collection.insertOne(mongoDoc.getDocument());
            }
            
            PR32CreateMain.logger.info("Inserted Correctly");
            System.out.println("\nInserted Correctly");
        } catch (Exception e) {
            PR32CreateMain.logger.warning("An error ocurred: " + e.getMessage());
            System.out.println("An error ocurred: " + e.getMessage());
        }
    }

    public List<MongoDoc> xmlToMongoDoc(String url) {     //Llegir dades
        MongoDoc resultado;
        List<MongoDoc> listaRes = new ArrayList<MongoDoc>();
        List<String> attributes = new ArrayList<>();

        //Vamo a leer 
        try {
            // Parse the XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(url);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get all 'post' nodes
            NodeList postList = doc.getElementsByTagName("post");

            for (int temp = 0; temp < postList.getLength(); temp++) {
                Node postNode = postList.item(temp);

                if (postNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element postElement = (Element) postNode;

                    // Get all 'field' nodes within this 'post'
                    NodeList fieldList = postElement.getChildNodes();

                    for (int count = 0; count < fieldList.getLength(); count++) {
                        Node fieldNode = fieldList.item(count);

                        if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element fieldElement = (Element) fieldNode;

                            // Get the 'name' and 'value' attributes of the 'field'
                            attributes.add(fieldElement.getTextContent());
                        }
                    }
                }
                
                //Obtindre dades y crear un doc
                String id = attributes.get(0);   
                String postTpeId = attributes.get(1);   
                String acceptedAnswerId = attributes.get(2);   
                String cDate = attributes.get(3);   
                String score = attributes.get(4);   
                String viewCount = attributes.get(5);   
                String body = StringEscapeUtils.unescapeHtml4(attributes.get(6));   //Aqui modifiquem el String
                String ownerUserId = attributes.get(7);    
                String lastDate = attributes.get(8);   
                String title = attributes.get(9); 
                String tags = attributes.get(10);    
                String ansCount = attributes.get(11);  
                String comCount = attributes.get(12);    
                String license = attributes.get(13);   

                resultado = new MongoDoc(id, postTpeId, acceptedAnswerId, cDate, score, viewCount, body, ownerUserId, lastDate, title, tags, ansCount, comCount, license);
                listaRes.add(resultado);
                attributes.clear();
            }

            PR32CreateMain.logger.info("Read Correctly");
            System.out.println("Read Correctly");
        } catch (Exception e) {
            PR32CreateMain.logger.warning("An error ocurred: " + e.getMessage());
            System.out.println("An error ocurred: " + e.getMessage());
            return null;
        }
        return listaRes;
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
            PR32CreateMain.logger.warning("An error ocurred: " + e.getMessage());
            System.out.println("An error ocurred: " + e.getMessage());
        }
        return null;
    }
}

