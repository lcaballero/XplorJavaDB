package XplorJavaDB;

import com.google.inject.Singleton;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.xml.bind.JAXBException;


@Singleton
public class App {

    public void start() throws JAXBException {
        DBI dbi = new DBI(new PGConn());
        Handle h = null;

        try {
            h = dbi.open();
        } finally {
            if (h != null) {
                h.close();
            }
        }
    }

    public void read() throws JAXBException {
        Database db = new XmlIO().read("db/database-classes.xml", Database.class);
    }

    private static void dbReading() throws Exception {
        new ReadDb().insertBoard();
        new ReadDb().readBoards();
    }
}