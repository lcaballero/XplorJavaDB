package XplorJavaDB.pg;

import com.google.common.base.Joiner;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class TableDescription implements ResultSetMapper<ColumnProperties> {

    private DBI dbi = null;

    /**
     * http://stackoverflow.com/questions/15928118/how-to-get-column-attributes-query-from-table-name-using-postgresql
     */
    public static final String TABLE_DESC = Joiner.on(" ").join(
        "SELECT DISTINCT",
            "a.attnum as column_position,",
            "a.attname as column_name,",
            "format_type(a.atttypid, a.atttypmod) as column_type,",
            "a.attnotnull as has_not_null_constraint,",
            "com.description as columnt_comment,",
            "coalesce(i.indisprimary,false) as is_primary_key,",
            "coalesce(i.indisunique,false) as is_unique,",
            "def.adsrc as column_default_text",
        "FROM pg_attribute a",
        "JOIN pg_class pgc ON pgc.oid = a.attrelid",
        "LEFT JOIN pg_index i ON",
            "(pgc.oid = i.indrelid AND i.indkey[0] = a.attnum)",
        "LEFT JOIN pg_description com on",
            "(pgc.oid = com.objoid AND a.attnum = com.objsubid)",
        "LEFT JOIN pg_attrdef def ON",
            "(a.attrelid = def.adrelid AND a.attnum = def.adnum)",
        "WHERE a.attnum > 0 AND pgc.oid = a.attrelid",
        "AND pg_table_is_visible(pgc.oid)",
        "AND NOT a.attisdropped",
        "AND pgc.relname = :table_name",
        "AND a.attname = :column_name",
        "ORDER BY a.attnum;");

    public TableDescription(DBI dbi) {
        if (dbi == null) {
            throw new IllegalArgumentException("Cannot provide table description with null DBI instance.");
        }
        this.dbi = dbi;
    }

    public Optional<ColumnProperties> getColumn(String tableName, String columnName) {
        try (Handle handle = this.dbi.open()) {
            List<ColumnProperties> list = handle.createQuery(TABLE_DESC)
                .bind("table_name", tableName)
                .bind("column_name", columnName)
                .map(this)
                .list();

            return list.stream().findFirst();
        }
    }

    @Override
    public ColumnProperties map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new ColumnProperties(index, r, ctx);
    }
}
