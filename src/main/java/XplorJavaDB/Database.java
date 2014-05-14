package XplorJavaDB;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Database {

    private int version;
    private String script;

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
