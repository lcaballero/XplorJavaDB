package XplorJavaDB;

import com.google.common.base.Joiner;

public class VersionScriptProvider {

    private static final String CREATE_VERSION_HISTORY_TABLE =
        Joiner.on("").join(
            "CREATE TABLE IF NOT EXIST %s",
            "(",
            "version int NOT NULL,",
            "username text,",
            "date_added timestamp without time zone",
            ");");

    private static final String SELECT_HISTORY =
        Joiner.on(" ").join(
            "SELECT version_number, username, date_added",
            "FROM %s",
            "ORDER BY version_number desc;");

    private static final String INSERT_HISTORY_ENTRY =
        Joiner.on(" ").join(
            "INSERT INTO %s",
                "(version_number, username, date_added)",
            "VALUES",
                "($1, $2, now())");

    public static final String DEFAULT_TABLE_NAME = "version_history";

    private String tableName = DEFAULT_TABLE_NAME;

    public VersionScriptProvider(String tableName) {
        this.tableName = tableName;
    }

    public VersionScriptProvider() {
        this(DEFAULT_TABLE_NAME);
    }

    public String getTableName() { return tableName; }

    public String getCreateVersionHistoryTable() {
        return String.format(CREATE_VERSION_HISTORY_TABLE, this.getTableName());
    }

    public String getSelectHistory() {
        return String.format(SELECT_HISTORY, this.getTableName());
    }

    public String getInsertHistoryEntry() {
        return String.format(INSERT_HISTORY_ENTRY, this.getTableName());
    }
}
