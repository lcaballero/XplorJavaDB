package XplorJavaDB;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VersionUpdatesMapper implements ResultSetMapper<VersionUpdate> {

    @Override
    public VersionUpdate map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        VersionUpdate u = new VersionUpdate();
        u.setVersion(r.getInt("version_number"));
        u.setUsername(r.getString("username"));
        u.setDateAdded(r.getTimestamp("date_added"));

        return u;
    }
}
