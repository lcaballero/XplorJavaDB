package XplorJavaDB.pg;

import XplorJavaDB.Helpers;
import com.google.common.base.Joiner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TableDescriptionTests extends Helpers {

    private static final String TEST_TABLE_NAME = "test_table";
    private static final String TEST_TABLE = String.format(Joiner.on(" ").join(
        "CREATE TABLE test_table",
        "(",
            "id int PRIMARY KEY,",
            "name text,",
            "email text NOT NULL UNIQUE",
        ")"
    ), TEST_TABLE_NAME);

    private static final String[] TEST_TABLES = new String[] { "hist", TEST_TABLE_NAME };

    @Before
    public void setup() {
        dbi = new DBI(new PGConn());
        activeTable = TEST_TABLE_NAME;
        dropTables(dbi, TEST_TABLES);
        execute(dbi, TEST_TABLE);
    }

    @After
    public void teardown() {
        dropTables(dbi, TEST_TABLES);
    }

    @Test (expected = IllegalArgumentException.class)
    public void ctor_should_not_accept_null_dbi() {
        new TableDescription(null);
    }

    @Test
    public void getColumnProperties_should_return_empty_optional_if_table_does_not_exist() {
        TableDescription desc = new TableDescription(dbi);
        Optional<ColumnProperties> p = desc.getColumn("hist", "id");

        assertThat(p.isPresent(), is(false));
    }

    @Test
    public void getColumnProperties_should_return_empty_optional_if_table_column_does_not_exist() {
        TableDescription desc = new TableDescription(dbi);

        // 'foo_bar_baz' isn't a column
        Optional<ColumnProperties> p = desc.getColumn(TEST_TABLE_NAME, "foo_bar_baz");

        assertThat(p.isPresent(), is(false));
    }

    @Test
    public void getColumntProperties_should_return_isPrimiary_true_for_primary_column() {
        TableDescription desc = new TableDescription(dbi);
        Optional<ColumnProperties> p = desc.getColumn(TEST_TABLE_NAME, "id");

        assertThat(p.isPresent(), is(true));
        assertThat(p.get().isPrimary(), is(true));
    }

    @Test
    public void getColumnProperties_should_be_integer_type_name_for_id_column() {
        TableDescription desc = new TableDescription(dbi);
        Optional<ColumnProperties> p = desc.getColumn(TEST_TABLE_NAME, "id");

        assertThat(p.get().isInteger(), is(true));
        assertThat(p.get().getTypeName(), is(ColumnProperties.TYPE_INTEGER));

        assertThat(p.get().isPrimary(), is(true));

        // Primary implies NOT NULL and UNIQUE
        assertThat(p.get().isNullable(), is(false));
        assertThat(p.get().isUnique(), is(true));
    }

    @Test
    public void getColumnProperties_should_be_text_type_name_for_name_column() {
        TableDescription desc = new TableDescription(dbi);
        Optional<ColumnProperties> p = desc.getColumn(TEST_TABLE_NAME, "name");

        assertThat(p.get().isText(), is(true));
        assertThat(p.get().getTypeName(), is(ColumnProperties.TYPE_TEXT));
    }

    @Test
    public void getColumnProperties_should_report_email_as_not_null_and_unique() {
        TableDescription desc = new TableDescription(dbi);
        Optional<ColumnProperties> p = desc.getColumn(TEST_TABLE_NAME, "email");

        assertThat(p.get().isText(), is(true));
        assertThat(p.get().isNullable(), is(false));
        assertThat(p.get().getTypeName(), is(ColumnProperties.TYPE_TEXT));
    }

}
