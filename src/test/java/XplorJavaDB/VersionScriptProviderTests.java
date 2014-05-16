package XplorJavaDB;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


public class VersionScriptProviderTests {

    @Test
    public void default_instance_should_have_default_table_name() {
        VersionScriptProvider v = new VersionScriptProvider();
        assertThat(v.getTableName(), notNullValue());
        assertThat(v.getTableName(), is(VersionScriptProvider.DEFAULT_TABLE_NAME));
    }

    @Test
    public void create_history_table_script_should_have_default_table_name() {
        VersionScriptProvider v = new VersionScriptProvider();
        assertThat(v.getCreateVersionHistoryTable(), containsString(v.getTableName()));
    }

    @Test
    public void history_query_should_have_default_table_name() {
        VersionScriptProvider v = new VersionScriptProvider();
        assertThat(v.getSelectHistory(), containsString(v.getTableName()));
    }

    @Test
    public void insert_history_script_should_have_default_table_name() {
        VersionScriptProvider v = new VersionScriptProvider();
        assertThat(v.getInsertHistoryEntry(), containsString(v.getTableName()));
    }

    @Test
    public void create_history_table_script_should_have_custom_table_name() {
        String table = "custom_table";
        VersionScriptProvider v = new VersionScriptProvider(table);
        assertThat(v.getCreateVersionHistoryTable(), containsString(v.getTableName()));
    }

    @Test
    public void history_query_should_have_custom_table_name() {
        String table = "custom_table";
        VersionScriptProvider v = new VersionScriptProvider(table);
        assertThat(v.getSelectHistory(), containsString(table));
    }

    @Test
    public void insert_history_script_should_have_custom_table_name() {
        String table = "custom_table";
        VersionScriptProvider v = new VersionScriptProvider(table);
        assertThat(v.getInsertHistoryEntry(), containsString(table));
    }
}
