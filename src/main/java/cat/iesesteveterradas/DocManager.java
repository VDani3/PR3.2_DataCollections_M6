package cat.iesesteveterradas;


import java.util.ArrayList;
import java.util.List;

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
            }
            
            //Obtindre dades
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

/*
 * declare option output:method "xml";
declare option output:indent "yes";

<posts>{
  for $p in /posts/row[@PostTypeId='1']
  let $viewCount := xs:integer($p/@ViewCount)
  order by $viewCount descending
  return 
    <post>
      <id>{$p/@Id/string()}</id>
      <posttypeid>{$p/@Id/string()}</posttypeid>
      <acceptedanswerid>{$p/@Id/string()}</acceptedanswerid>
      <creationdate></creationdate>
      <score></score>
      <viewcount></viewcount>
      <body></body>
      <owneruserid></owneruserid>
      <lastactivitydate></lastactivitydate>
      <title></title>
      <tags></tags>
      <answercount></answercount>
      <commentcount></commentcount>
      <contentlicense></contentlicense>
    </post>
}</posts>
 */