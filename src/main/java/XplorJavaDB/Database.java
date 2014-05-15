package XplorJavaDB;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Database {

    private int version;
    private String script;

    public Database(int version, String script) {
        this.version = version;
        this.script = script;
    }

    public Database() {
    }

    @XmlElement
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    @XmlElement
    public String getScript() { return script; }
    public void setScript(String script) { this.script = script; }

    @Override
    public String toString() {
        return String.format("version-version: %d\nsql-script:\n %s", version, script);
    }
}
