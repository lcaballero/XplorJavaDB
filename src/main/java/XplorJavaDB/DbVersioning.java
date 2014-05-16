package XplorJavaDB;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class DbVersioning {

    public static final String DEFAULT_USERNAME = "default-username";

    private DBI dbi = null;
    private String username = DEFAULT_USERNAME;
    private IScriptProvider scriptProvider = new VersionScriptProvider();
    private boolean hasCreatedHistoryTable = false;
    private IPreTransitionCheck checks;

    public DbVersioning(DBI dbi, IScriptProvider scriptProvider, IPreTransitionCheck checks, String username) {
        this();

        if (dbi == null) { throw new IllegalArgumentException("DBI must be non-null"); }
        if (scriptProvider == null) { throw new IllegalArgumentException("VersionScriptProvider cannot be null"); }
        if (checks == null) { throw new IllegalArgumentException("IPreTransitionChecks cannot be null"); }
        if (username == null || username.trim().isEmpty()) { throw new IllegalArgumentException("username cannot be null or empty"); }

        this.dbi = dbi;
        this.username = username;
        this.scriptProvider = scriptProvider;
        this.checks = checks;
    }

    public DbVersioning() {
        this.checks = (checks == null ? new TransitionChecks() : checks);
    }

    public String getUsername() { return username; }
    public IScriptProvider getScriptProvider() { return scriptProvider; }

    public List<VersionUpdate> getVersionUpdates() {
        try (Handle handle = dbi.open()) {
            return handle.createQuery(this.scriptProvider.getSelectHistory())
                .map(new VersionUpdatesMapper())
                .list();
        }
    }

    public void createHistoryTable() {
        if (hasCreatedHistoryTable) { return; }
        try (Handle handle = dbi.open()) {
            handle.execute(this.scriptProvider.getCreateVersionHistoryTable());
            hasCreatedHistoryTable = true;
        }
    }

    /**
     * Retrieves the version of the most current design which has been
     * applied to the database.
     */
    public int getCurrentVersion() {

        createHistoryTable();

        Optional<VersionUpdate> a = getVersionUpdates()
            .stream()
            .findFirst();

        return a.isPresent() ? a.get().getVersion() : 0;
    }

    /**
     * This executes the update to the database enforcing that the versions increment
     * monotonically.
     *
     * @param version The version of the update script.
     * @param script  The script which alters or creates artifacts to support the given version.
     * @return true iff the update was successful.
     */
    public boolean execute(int version, String script) {

        if (script == null || "".equals(script)) {
            throw new IllegalArgumentException("Cannot execute null or empty script.");
        }

        if (version < 0) {
            throw new IllegalArgumentException("Cannot execute script for negative version.");
        }

        try (Handle handle = dbi.open()) {
            handle.execute(script);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * This method updates meta-data held by the DB indicating which update versions
     * were ran, and when.  This table then provides the history of the
     * Database for debugging purposes.
     *
     * @param version  The version applied
     * @param username The username of the person who ran the version update.
     */
    public void updateHistory(int version, String username) {
        createHistoryTable();
        try (Handle handle = dbi.open()) {
            handle.createStatement(this.scriptProvider.getInsertHistoryEntry())
                .bind("version_number", version)
                .bind("username", username)
                .execute();
        }
    }

    /**
     * Updates the DB to the given version based on the list of iterative designs.
     * It walks through the designs one by one, applying them to the DB from the
     * DBs current version up to the targetVersion where each version is one held
     * by the list of Designs.
     *
     * @param designs       The list of designs that constitute updating the DB to a
     *                      a given version.
     * @param targetVersion The target version to lift the DB to.
     * @return true if all designs were successfully applied, and false otherwise.
     */
    public boolean toTargetVersion(List<Database> designs, int targetVersion) {

        this.checks.runChecks(designs, targetVersion);

        if (dbi == null) {
            throw new IllegalStateException("Cannot transition DB to target version if DBI instance is null");
        }

        int currentVersion = this.getCurrentVersion();

        List<Database> requiredUpdates = intermediateDesigns(designs, currentVersion, targetVersion);
        List<Database> sortedDesigns = orderVersions(requiredUpdates);

        sortedDesigns.stream().forEach((d) -> {
            this.execute(d.getVersion(), d.getScript());
            this.updateHistory(d.getVersion(), this.getUsername());
        });

        return true;
    }

    /**
     * Orders the list based on Version property.
     *
     * @param designs A list of items where a version is associated with a DB creation
     *                or altering scripts.
     * @return A list of the items ordered by version number.
     */
    public List<Database> orderVersions(List<Database> designs) {
        return
            (designs == null ? new ArrayList<Database>() : designs)
                .stream()
                .sorted((a, b) -> a.getVersion() - b.getVersion())
                .collect(Collectors.toList());
    }

    /**
     * Processes the given designs to produce a new list of designs where
     * the new list will include designs with versions greater than the current
     * version and less than or equal to the target version.  This list is
     * intended to be executed in order to produce a final DB state that
     * reflects the target version (often the latest version).
     *
     * @param designs        Database instances with version and script.
     * @param currentVersion The current version of the DB to determine a point of
     *                       reference and subsequent version can be produced
     *                       from this method.
     * @param targetVersion  The target version represents the final state we want
     *                       to see the DB updated to.
     * @return A list of designs greater than the current version and less than
     * or equal to the target version.  In the case where this method
     * is handed null it will return an empty list.
     */
    public List<Database> intermediateDesigns(List<Database> designs, int currentVersion, int targetVersion) {

        if (currentVersion > targetVersion) {
            throw new IllegalArgumentException(
                "Cannot find intermediate designs if current version greater than target version");
        }

        if (currentVersion < 0) {
            throw new IllegalArgumentException(
                "Cannot process a negative current version");
        }

        // This condition is impossible based on the above checks but left here for documentation
        // because essentially we want to reject a negative targetVersion
        if (targetVersion < 0) {
            throw new IllegalArgumentException(
                "Cannot process a negative target version");
        }

        // Return an empty list if this method is given a null list.
        return (designs == null ? new ArrayList<Database>() : designs)
            .stream()
            .filter((d) -> d.getVersion() > currentVersion && d.getVersion() <= targetVersion)
            .collect(Collectors.toList());
    }
}
