package XplorJavaDB;

import java.sql.Timestamp;

public class VersionUpdate implements Comparable<VersionUpdate> {

    private int version;
    private String username;
    private Timestamp dateAdded;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public int compareTo(VersionUpdate o) {
        return o.version - this.version;
    }
}
