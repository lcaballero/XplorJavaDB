package XplorJavaDB.pg;

import XplorJavaDB.Helpers;
import com.google.common.base.Joiner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;


public class TableDescription_TypeTests extends Helpers {

    private static final String TYPE_TABLE_NAME = "type_table_testing";
    private static final String[] TEST_TABLES = new String[] {
        TYPE_TABLE_NAME
    };
    private static final String TYPE_TABLE = String.format(Joiner.on(" ").join(
        "CREATE TABLE %s",
        "(",
            "id int PRIMARY KEY,",                      // test: int, primary, unique
            "name text,",                               // test: text
            "email text NOT NULL UNIQUE,",              // test: is nullable, unique
            "created_on timestamp,",                    // test: timestamp without time zone
            "inserted_on timestamp with time zone,",    // test: timestamp with time zone
            "is_active boolean",                        // test: boolean
        ")"
    ), TYPE_TABLE_NAME);

    @Before
    public void setup() {
        dbi = new DBI(new PGConn());
        activeTable = TYPE_TABLE_NAME;
        execute(dbi, dropScript("hist"));
        execute(dbi, TYPE_TABLE);
    }


    @After
    public void teardown() {
        dropTables(dbi, TEST_TABLES);
    }

    @Test
    public void should_find_id_is_type_integer() {
        hasType("id", ColumnProperties.TYPE_INTEGER,
            ColumnProperties::isInteger,
            ColumnProperties::isPrimary,
            ColumnProperties::isUnique);
    }

    @Test
    public void should_find_email_is_type_text() {
        hasType("email", ColumnProperties.TYPE_TEXT,
            ColumnProperties::isText,
            ColumnProperties::isUnique,
            (p) -> !p.isNullable());
    }

    @Test
    public void should_find_created_on_is_type_timestamp_wo_tz() {
        hasType("created_on", ColumnProperties.TYPE_TIMESTAMP_WITHOUT_TZ,
            ColumnProperties::isTimestampWithoutTz);
    }

    @Test
    public void should_find_created_on_is_type_timestamp_with_tz() {
        hasType("inserted_on", ColumnProperties.TYPE_TIMESTAMP_WITH_TZ,
            ColumnProperties::isTimestampWithTz);
    }

    @Test
    public void should_find_is_active_is_type_boolean() {
        hasType("is_active", ColumnProperties.TYPE_BOOLEAN,
            ColumnProperties::isBoolean);
    }
}
