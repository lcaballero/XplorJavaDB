package XplorJavaDB;


import org.joda.time.DateTime;
import org.postgresql.*;
import org.skife.jdbi.v2.DBI;

import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Stream;

public class ReadDb {

    private boolean hasRegisteredDriver = false;

    public void registerDriver() throws SQLException {
        if (hasRegisteredDriver) { return; }
        DriverManager.registerDriver(new org.postgresql.Driver());
    }

    public Properties getConnectionProperties() {
        Properties props = new Properties();
        props.setProperty("user", "lucas.caballero");

        return props;
    }

    public String getConnectionUrl() {
        String url = "jdbc:postgresql://localhost:5432/xploring_java";
        return url;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getConnectionUrl(), getConnectionProperties());
    }

    public void read() throws SQLException, ClassNotFoundException {

        Connection conn = getConnection();

        PreparedStatement pstmt = conn.prepareStatement("select id, game_id, user_id, name, state, active from board");

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String s = rs.getString("id");
            System.out.println(s);
        }
    }

    public void readBoards() throws Exception {
        DBI dbi = new DBI(getConnectionUrl(), getConnectionProperties());
        ReadBoards b = dbi.open(ReadBoards.class);

        b.readBoards().stream().forEach(System.out::println);

        b.close();
    }

    public void insertBoard() {
        DBI dbi = new DBI(getConnectionUrl(), getConnectionProperties());
        ReadBoards b = dbi.open(ReadBoards.class);

        b.insert(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "another-me",
                1,
                2,
                new Timestamp(DateTime.now().getMillis()));
    }
}
