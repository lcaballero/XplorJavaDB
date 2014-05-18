package XplorJavaDB.pg;

import org.skife.jdbi.v2.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnProperties {

    public static final String COL_POSITION = "column_position";
    public static final String COL_NAME = "column_name";
    public static final String COL_TYPENAME = "column_type";
    public static final String COL_HAS_NOT_NULL_CONSTRAINT = "has_not_null_constraint";
    public static final String COL_IS_PRIMARY_KEY = "is_primary_key";
    public static final String COL_IS_UNIQUE = "is_unique";
    public static final String COL_DEFAULT_TEXT = "column_default_text";

    public static final String TYPE_INTEGER = "integer";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_TIMESTAMP_WITHOUT_TZ = "timestamp without time zone";
    public static final String TYPE_TIMESTAMP_WITH_TZ = "timestamp with time zone";

    private int position;
    private String columnName;
    private String typeName;
    private boolean isNullable;
    private String columnText;
    private boolean isPrimary;
    private boolean isUnique;
    private String defaultText;

    public ColumnProperties(int index, ResultSet r, StatementContext ctx) throws SQLException {
        position = r.getInt(COL_POSITION);
        columnName = r.getString(COL_NAME);
        typeName = r.getString(COL_TYPENAME);
        columnText = r.getString(COL_IS_PRIMARY_KEY);
        isPrimary = r.getBoolean(COL_IS_PRIMARY_KEY);
        isUnique = r.getBoolean(COL_IS_UNIQUE);
        defaultText = r.getString(COL_DEFAULT_TEXT);

        // If it has the NOT NULL constraint then it is not nullable
        isNullable = !r.getBoolean(COL_HAS_NOT_NULL_CONSTRAINT);
    }

    public ColumnProperties() {
    }

    public String getDefaultText() { return defaultText; }
    public boolean isPrimary() { return isPrimary; }
    public boolean isUnique() { return isUnique; }
    public String getColumnText() { return columnText; }
    public boolean isNullable() { return isNullable; }

    public String getTypeName() { return typeName; }
    public boolean isInteger() { return isType(TYPE_INTEGER); }
    public boolean isText() { return isType(TYPE_TEXT); }
    public boolean isBoolean() { return isType(TYPE_BOOLEAN); }
    public boolean isTimestampWithoutTz() { return isType(TYPE_TIMESTAMP_WITHOUT_TZ); }
    public boolean isTimestampWithTz() { return isType(TYPE_TIMESTAMP_WITH_TZ); }

    public String getColumnName() { return columnName; }
    public int getPosition() { return position; }

    public boolean isType(String type) {
        return (type == null ? "" : type).equals(typeName);
    }
}
