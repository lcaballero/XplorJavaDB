package XplorJavaDB;

import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


public class DbVersioningTests extends Helpers {

    @Test (expected = IllegalArgumentException.class)
    public void ctor_should_not_accept_null_dbi_instance() {
        new DbVersioning(null, new VersionScriptProvider(), new TransitionChecks(), "test.user");
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctor_should_not_accept_null_script_provider_instance() {
        new DbVersioning(new DBI(new PGConn()), null, new TransitionChecks(), "test.user");
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctor_should_not_accept_null_checks_instance() {
        new DbVersioning(new DBI(new PGConn()), new VersionScriptProvider(), null, "test.user");
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctor_should_not_accept_null_username_instance() {
        new DbVersioning(new DBI(new PGConn()), new VersionScriptProvider(), new TransitionChecks(), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctor_should_not_accept_empty_username_instance() {
        new DbVersioning(new DBI(new PGConn()), new VersionScriptProvider(), new TransitionChecks(), "  ");
    }

    @Test
    public void should_be_able_to_insert_into_history() {
        VersionScriptProvider provider = new VersionScriptProvider();
        DBI dbi = new DBI(new PGConn());

        try (Handle handle = dbi.open()) {
            handle.execute(provider.getCreateVersionHistoryTable());
            handle.createStatement(provider.getInsertHistoryEntry())
                .bind("version_number", 1)
                .bind("username", "lucas.caballero")
                .execute();

            handle.execute(dropScript(provider.getTableName()));
        }
    }

    @Test
    public void after_running_toTargetVersion_the_update_history_table_should_reflect_the_target_as_the_newest_version() {

        DBI dbi = new DBI(new PGConn());
        VersionScriptProvider provider = new VersionScriptProvider();

        try (Handle handle = dbi.open()) {

            // Drop history table so that we can insert updates, and make sure the process
            // initializes the table for further inserts.
            handle.execute(dropScript(provider.getTableName()));

            List<Database> designs = basicDesigns();

            DbVersioning dbv = new DbVersioning(dbi, provider, new TransitionChecks(), "testing-framework");
            dbv.toTargetVersion(designs, 2);

            List<VersionUpdate> updates = dbv.getVersionUpdates();

            Optional<VersionUpdate> update = updates.stream().sorted().findFirst();

            assertThat(updates.isEmpty(), is(false));
            assertThat(updates.size(), is(designs.size()));

            Database last = designs.get(designs.size() - 1);
            assertThat(update.get().getVersion(), is(last.getVersion())); // last

            handle.execute(dropScript(provider.getTableName()));
            handle.execute(dropScript("something"));
            handle.execute(dropScript("user_profiles"));
        }
    }

    @Test
    public void toTargetVersion_should_show_the_test_framework_as_user_for_each_update() {

        DBI dbi = new DBI(new PGConn());
        VersionScriptProvider provider = new VersionScriptProvider();

        List<Database> designs = basicDesigns();

        String username = "test.framework.user";
        DbVersioning dbv = new DbVersioning(dbi, provider, new TransitionChecks(), username);
        dbv.toTargetVersion(designs, 2);

        List<VersionUpdate> updates = dbv.getVersionUpdates();

        boolean haveUsernames = updates.stream()
            .allMatch((u) -> u.getUsername().equals(username));

        assertThat(haveUsernames, is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void toTargetVersion_should_throw_exception_if_dbi_is_not_provided() {
        DbVersioning v = new DbVersioning();
        List<Database> versions = dbVersionRange(0, 10);
        v.toTargetVersion(versions, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toTargetVersion_should_throw_exception_if_any_designs_include_null_scripts() {
        DbVersioning v = new DbVersioning();
        List<Database> versions = dbVersionRange(0, 10);

        Database db = new Database();
        db.setVersion(11);
        db.setScript(null); // <= purposefully setting the test criteria

        versions.add(db);

        v.toTargetVersion(versions, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toTargetVersion_should_throw_exception_if_any_designs_include_empty_scripts() {
        DbVersioning v = new DbVersioning();
        List<Database> versions = dbVersionRange(0, 10);

        Database db = new Database();
        db.setVersion(11);
        db.setScript(""); // <= purposefully setting the test criteria

        versions.add(db);

        v.toTargetVersion(versions, 7);
    }


    @Test(expected = IllegalArgumentException.class)
    public void toTargetVersion_should_throw_exception_any_scripts_are_duplicates() {
        DbVersioning v = new DbVersioning();
        List<Database> versions = dbVersionRange(0, 10);

        Database db = new Database();
        db.setVersion(11);
        db.setScript("SELECT 7");  // <= purposefully setting the test criteria

        versions.add(db);

        v.toTargetVersion(versions, 11);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toTargetVersion_should_throw_exception_exist_duplicate_update_versions() {
        DbVersioning v = new DbVersioning();
        List<Database> versions = dbVersionRange(0, 10);

        Database db = new Database();
        db.setVersion(7);  // <= purposefully setting the test criteria
        db.setScript("SELECT * from something;");

        versions.add(db);

        v.toTargetVersion(versions, 7);
    }

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

    @Test
    public void getVersionsUpdate_should_return_empty_list_if_there_no_updates() {

        DBI dbi = new DBI(new PGConn());
        VersionScriptProvider provider = new VersionScriptProvider();

        try (Handle handle = dbi.open()) {

            handle.execute(dropScript(provider.getTableName()));

            String username = "test.framework.user";
            DbVersioning dbv = new DbVersioning(dbi, provider, new TransitionChecks(), username);
            dbv.createHistoryTable();

            List<VersionUpdate> history = dbv.getVersionUpdates();

            assertThat(history.isEmpty(), is(true));

            handle.execute(dropScript(provider.getTableName()));
        }
    }

    @Test
    public void getVersionsUpdate_should_return_full_list_of_history_updates() {

        DBI dbi = new DBI(new PGConn());
        VersionScriptProvider provider = new VersionScriptProvider();

        try (Handle handle = dbi.open()) {

            handle.execute(dropScript(provider.getTableName()));

            int i = 0;
            String default_user = "default.user";
            DbVersioning dbv = new DbVersioning(dbi, provider, new TransitionChecks(), default_user);
            dbv.updateHistory(++i, default_user);
            dbv.updateHistory(++i, default_user);
            dbv.updateHistory(++i, default_user);
            dbv.updateHistory(++i, default_user);

            List<VersionUpdate> history = dbv.getVersionUpdates();

            assertThat(history, notNullValue());
            assertThat(history.isEmpty(), is(false));
            assertThat(history.size(), is(i));

            handle.execute(dropScript(provider.getTableName()));
        }
    }

    @Test
    public void getScriptProvider_should_provide_non_null_for_default_instance() {
        DbVersioning v = new DbVersioning();
        assertThat(v.getScriptProvider(), notNullValue());
    }
}
