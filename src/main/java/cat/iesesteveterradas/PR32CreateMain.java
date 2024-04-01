package cat.iesesteveterradas;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PR32CreateMain {
    public static final Logger logger = LoggerFactory.getLogger(PR32CreateMain.class);    

    public static void main(String[] args) throws IOException {
        //Variables
        DocManager dm = new DocManager();
        String dbUrl = "mongodb://root:example@localhost:27017";
        String dbName = "japanese";
        String collection = "collection";
        ArrayList<String> documents = new ArrayList<>();
        
        //Afegir els arxius que volem
        documents.add("/data/input/example.xml");

        //Inserir
        for (String s : documents) {
            dm.insertMongo(dbUrl, dbName, collection, dm.xmlToMongoDoc(s));
        }


    }


}

