package XplorJavaDB;

import com.google.common.base.Joiner;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name="databases")
public class Databases {

    private List<Database> databases = new ArrayList<>();

    @XmlElement(name="database")
    public List<Database> getDatabases() { return databases; }
    public void setDatabases(List<Database> databases) { this.databases = databases; }

    @Override
    public String toString() {
        return Joiner.on("\n").join(databases.toArray());
    }
}
