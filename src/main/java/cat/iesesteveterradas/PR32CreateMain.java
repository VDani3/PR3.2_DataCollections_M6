package cat.iesesteveterradas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;



public class PR32CreateMain {
    public static final Logger logger = Logger.getLogger(PR32CreateMain.class.getName());   

    public static void main(String[] args) throws IOException {

        DocManager.setupLogger();
       
        //Variables
        DocManager dm = new DocManager();
        String dbUrl = "mongodb://root:example@localhost:27017";
        String dbName = "japanese";
        String collection = "collection";
        ArrayList<String> documents = new ArrayList<>();
        
        //Afegir els arxius que volem
        documents.add(System.getProperty("user.dir") +"/data/input/info.xml");

        //Inserir
        for (String s : documents) {
            dm.insertMongo(dbUrl, dbName, collection, dm.xmlToMongoDoc(s));
        }


    }


}

