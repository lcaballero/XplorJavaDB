package XplorJavaDB;

import com.google.common.base.Joiner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.util.IntegerMapper;
import org.skife.jdbi.v2.util.StringMapper;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;


public class PGConnTest {

    private DBI dbi = null;
    private Handle handle = null;

    @Before
    public void setup() {
        DBI dbi = new DBI(new PGConn());
        handle = dbi.open();
    }

    @After
    public void teardown() {
        if (handle != null) {
            handle.close();
        }
    }

    @Test
    public void should_find_updates() {

        String sql = Joiner.on(" ").join(
            "SELECT version_number, username, date_added",
            "FROM version_updates",
            "ORDER BY version_number DESC");

        Query<Map<String,Object>> q = handle.createQuery(sql);

        Query<VersionUpdates> vq = q.map(new VersionUpdatesMapper());
        List<VersionUpdates> updates = vq.list();

        assertThat(updates.size(), greaterThan(0));
        assertThat(updates.get(0).getVersion(), is(10));
    }

    @Test
    public void should_be_able_to_open_dbi_connection_with_no_issues() {
        Query<Map<String, Object>> q = handle.createQuery("SELECT 1");
        Query<String> q1 = q.map(StringMapper.FIRST);
        List<String> rs = q1.list();

        assertThat(rs.get(0), is("1"));
    }

    @Test
    public void should_create_table_insert_1_row_then_read_id() {
        handle.execute("create table something (id int)");
        handle.execute("insert into something (id) values (42)");

        Query<Map<String, Object>> q = handle.createQuery("SELECT id from something;");
        Query<Integer> q1 = q.map(IntegerMapper.FIRST);
        List<Integer> rs = q1.list();

        assertThat(rs.get(0), is(42));

        handle.execute("drop table something;");
    }
}
