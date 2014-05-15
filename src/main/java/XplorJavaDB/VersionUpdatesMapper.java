package XplorJavaDB;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VersionUpdatesMapper implements ResultSetMapper<VersionUpdates> {

    @Override
    public VersionUpdates map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        VersionUpdates u = new VersionUpdates();
        u.setVersion(r.getInt("version_number"));
        u.setUsername(r.getString("username"));
        u.setDateAdded(r.getTimestamp("date_added"));

        return u;
    }
}
