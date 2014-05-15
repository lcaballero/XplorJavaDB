package XplorJavaDB;

import com.google.common.base.Joiner;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;


public class PGConn implements ConnectionFactory {

    // http://www.postgresql.org/docs/9.3/static/libpq-connect.html#AEN39024
    public static final String DEFAULT_CONNECTION_URL = "jdbc:postgresql://localhost:5432/xploring_java";

    private String url = DEFAULT_CONNECTION_URL;

    public PGConn() {}

    public PGConn(String driver, String protocol, String host, int port, String databaseName) {
        url = Joiner.on("").join(driver, protocol, host, "" + port, databaseName);
    }

    public Properties getConnectionProperties() {
        Map<String,String> env = System.getenv();
        Properties props = new Properties();
        props.setProperty("user", env.get("PG_USER"));
        props.setProperty("password", env.get("PG_PASS"));
        return props;
    }

    public String getConnectionUrl() {
        return url;
    }

    @Override
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(getConnectionUrl(), getConnectionProperties());
    }

    class Design {
        public int version;
        public String script;
    }


}
