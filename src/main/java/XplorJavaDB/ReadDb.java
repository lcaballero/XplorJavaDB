package XplorJavaDB;


import XplorJavaDB.pg.PGConn;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;

import java.sql.*;
import java.util.UUID;

public class ReadDb {

    public void read() throws SQLException, ClassNotFoundException {

        Connection conn = new PGConn().openConnection();

        PreparedStatement pstmt = conn.prepareStatement("select id, game_id, user_id, name, state, active from board");

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String s = rs.getString("id");
            System.out.println(s);
        }
    }

    public void readBoards() throws Exception {
        DBI dbi = new DBI(new PGConn());
        ReadBoards b = dbi.open(ReadBoards.class);

        b.readIds().stream().forEach(System.out::println);

        b.close();
    }

    public void insertBoard() {
        DBI dbi = new DBI(new PGConn());
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
