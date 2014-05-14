package XplorJavaDB;

import com.google.gson.Gson;
import com.google.inject.Singleton;

import javax.xml.bind.JAXBException;


@Singleton
public class App {

    public void start() throws JAXBException {
        Database db = new XmlIO().read("db/database-classes.xml", Database.class);
    }

    private static void dbReading() throws Exception {
        new ReadDb().insertBoard();
        new ReadDb().readBoards();
    }
}