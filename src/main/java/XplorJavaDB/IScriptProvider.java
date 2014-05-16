package XplorJavaDB;

public interface IScriptProvider {
    String DEFAULT_TABLE_NAME = "version_history";

    String getTableName();

    String getCreateVersionHistoryTable();

    String getSelectHistory();

    String getInsertHistoryEntry();
}
