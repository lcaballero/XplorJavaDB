package XplorJavaDB;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


public interface ReadBoards extends AutoCloseable {

    @SqlQuery("select id from board")
    public List<String> readIds();

    public static final String insert_sql =
        "insert into board" +
            "(id, game_id, user_id, name, state, active, date_added)" +
        "values" +
            "(:id, :game_id, :user_id, :name, :state, :active, :date_added)";

    @SqlUpdate(insert_sql)
    public void insert(
            @Bind("id") UUID id,
            @Bind("game_id") UUID game_uuid,
            @Bind("user_id") UUID user_uuid,
            @Bind("name") String name,
            @Bind("state") int state,
            @Bind("active") int active,
            @Bind("date_added") Timestamp date_added);
}
