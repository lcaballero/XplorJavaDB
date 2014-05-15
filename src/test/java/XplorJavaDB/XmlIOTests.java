package XplorJavaDB;

import org.junit.Test;

import javax.xml.bind.JAXBException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;


public class XmlIOTests {

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_file_that_does_not_exist() throws JAXBException {
        new XmlIO().read("files/does-not-exist.xml", Database.class);
    }

    @Test
    public void should_find_2_databases_in_multi_db_file() throws JAXBException {
        Databases c = new XmlIO().read("files/multi-database.xml", Databases.class);
        assertThat(c.getDatabases().size(), is(2));
    }

    @Test
    public void should_find_minimum_data_in_single_db_file() throws JAXBException {
        Database c = new XmlIO().read("files/single-database.xml", Database.class);

        assertThat(c.getScript(), notNullValue());
        assertThat(c.getScript().length(), greaterThan(10));
        assertThat(c.getVersion(), is(1));
    }
}
