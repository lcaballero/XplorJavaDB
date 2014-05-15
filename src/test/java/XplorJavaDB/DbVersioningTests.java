package XplorJavaDB;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


public class DbVersioningTests {

    @Test
    public void getUsername_should_start_with_default() {
        DbVersioning v = new DbVersioning();
        assertThat(v.getUsername(), is(DbVersioning.DEFAULT_USERNAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_db_list_is_null() {
        DbVersioning db = new DbVersioning();
        db.toTargetVersion(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_target_version_is_not_in_databases() {
        List<Database> list = new ArrayList<>();
        Database db = new Database();
        db.setVersion(1);
        list.add(db);
        new DbVersioning().toTargetVersion(list, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toTargetVersion_should_throw_exception_if_target_version_is_negative() {
        List<Database> list = new ArrayList<>();
        new DbVersioning().toTargetVersion(list, -1);
    }

    public List<Database> dbVersionRange(int from, int to) {
        return IntStream.range(from, to)
            .mapToObj((n) -> {
                Database db = new Database();
                db.setVersion(n);
                return db;
            })
            .collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void intermediateDesigns_should_throw_exception_if_target_version_less_than_current_version() {
        List<Database> designs = dbVersionRange(0, 10);
        new DbVersioning().intermediateDesigns(designs, 5, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void intermediateDesigns_should_throw_exception_if_target_version_is_negative() {
        List<Database> designs = dbVersionRange(0, 10);
        new DbVersioning().intermediateDesigns(designs, -15, -7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void intermediateDesigns_should_throw_exception_if_current_version_is_negative() {
        List<Database> designs = dbVersionRange(0, 10);
        new DbVersioning().intermediateDesigns(designs, -1, -2);
    }

    @Test
    public void intermediateDesigns_should_not_fail_for_empty_list_since_thats_a_precondition() {
        List<Database> required = new DbVersioning().intermediateDesigns(null, 0, 1);
        assertThat(required, notNullValue());
        assertThat(required.size(), is(0));
    }

    @Test
    public void intermediateDesigns_should_return_all_lte_to_current() {
        List<Database> designs = dbVersionRange(0, 10);
        List<Database> required = new DbVersioning().intermediateDesigns(designs, 0, 2);

        assertThat(required, notNullValue());
        assertThat(required.size(), is(2));

        // Doesn't include version 0 because the low end is exclusive.
        // The resulting range from intermediateDesigns is (current-version, target-version].
        // Low exclusive and high inclusive.
        assertThat(required.get(0).getVersion(), is(1));
        assertThat(required.get(1).getVersion(), is(2));
    }

    @Test
    public void orderVersions_should_not_fail_if_passed_null() {
        List<Database> dbs = new DbVersioning().orderVersions(null);
        assertThat(dbs, notNullValue());
    }

    @Test
    public void orderVersions_should_order_database_instances_based_on_version() {

        int[] ids = new int[] { 4, 2, 3, 1, 10 };

        List<Database> designs = IntStream.of(ids)
            .mapToObj((n) -> new Database(n, null))
            .collect(Collectors.toList());

        designs = new DbVersioning().orderVersions(designs);

        assertThat(designs, notNullValue());
        assertThat(designs.size(), is(ids.length));

        Arrays.sort(ids);

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            Database db = designs.get(i);
            assertThat(
                String.format("id: %d, i: %d", id, i),
                db.getVersion(), is(id));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void execute_should_throw_exception_if_script_is_null() {
        DbVersioning v = new DbVersioning();
        v.execute(0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void execute_should_throw_exception_if_script_is_empty() {
        DbVersioning v = new DbVersioning();
        v.execute(0, "");
    }
}
