package XplorJavaDB;
 
import XplorJavaDB.pg.PGConn;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;


public class FakeCreationTests {

    DBI dbi = null;

    @Before
    public void setup() {
        dbi = new DBI(new PGConn());
    }
    
    @Test
    public void should_fail_reading_from_non_existent_table() {
        
    }

    @Test
    public void should_lambda() {

    }
}
