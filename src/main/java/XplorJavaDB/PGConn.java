package XplorJavaDB;

import com.google.common.base.Joiner;
import org.skife.jdbi.v2.tweak.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * Implements a connection factory suiteable for JDBI, which is prepares to connect
 * to a PostGre DB via JDBC.
 */
public class PGConn implements ConnectionFactory {

    /**
     * See http://www.postgresql.org/docs/9.3/static/libpq-connect.html#AEN39024
     */
    public static final String DEFAULT_CONNECTION_URL = "jdbc:postgresql://localhost:5432/xploring_java";

    private String url = DEFAULT_CONNECTION_URL;

    /**
     * Uses the default connection string.
     */
    public PGConn() {}

    /**
     * Constructs the connection string from the provided parts.
     *
     * @param driver The driver to use (jdbc).
     * @param protocol The protocol to use (postgresql).
     * @param host The host to use (localhost).
     * @param port The port to use (5432).
     * @param databaseName The db name to use (xploring_java).
     */
    public PGConn(String driver, String protocol, String host, int port, String databaseName) {
        url = Joiner.on("").join(driver, ":", protocol, "://", host, ":", port, "/", databaseName);
    }

    /**
     * Creates a Properties instance with user and password as found in the
     * environment as PG_USER and PG_PASS.
     *
     * @return A properties instance with user and password pulled from
     *         the environment.
     */
    public Properties getConnectionProperties() {
        Map<String,String> env = System.getenv();
        Properties props = new Properties();
        props.setProperty("user", env.get("PG_USER"));
        props.setProperty("password", env.get("PG_PASS"));
        return props;
    }

    /**
     * @return gets the url, either the default, or the one made from the parts.
     */
    public String getConnectionUrl() {
        return url;
    }

    /**
     * Creates a new Connection based on the getConnectionProperties and getConnectionUrl.
     *
     * @return A newly obtained connection.
     * @throws SQLException
     */
    @Override
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(getConnectionUrl(), getConnectionProperties());
    }
}
