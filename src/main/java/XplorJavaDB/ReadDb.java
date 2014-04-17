package XplorJavaDB;


import org.postgresql.*;

import java.sql.*;
import java.util.Properties;

public class ReadDb {

    public void read() throws SQLException, ClassNotFoundException {


        DriverManager.registerDriver(new org.postgresql.Driver());

//        Connection conn = DriverManager.getConnection(
//                "Drive=PostgreSQL;uid=lucas.caballero;pwd=livebig6; "
//                + "Host=localhost; Port=5432; DataBase=xploring_java");

        Properties props = new Properties();
        props.setProperty("user", "lucas.caballero");
        props.setProperty("password", "!!Livebig6");

        String url = "jdbc:postgresql://localhost:5432/xploring_java";

        Connection conn = DriverManager.getConnection(url, props);

        PreparedStatement pstmt = conn.prepareStatement("select id, game_id, user_id, name, state, active from board");

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String s = rs.getString("id");
            System.out.println(s);
        }
    }
}
