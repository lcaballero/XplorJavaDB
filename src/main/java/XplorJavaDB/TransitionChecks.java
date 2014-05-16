package XplorJavaDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TransitionChecks implements IPreTransitionCheck {

    private List<IDbDesignCheck> checks = new ArrayList<>();

    public TransitionChecks(List<IDbDesignCheck> checks) {
        this();
        this.checks = checks;
    }

    public TransitionChecks() {
        // Setup checks
        checks.add(this::checkNullDesigns);
        checks.add(this::checkNonNegativeTargetVersion);
        checks.add(this::checkTargetVersionExists);
        checks.add(this::checkAllNonNullScripts);
        checks.add(this::checkNoDuplicateVersions);
        checks.add(this::checkForDuplicateScripts);
    }

    @Override
    public void runChecks(List<Database> designs, int targetVersion) {
        for (IDbDesignCheck check : checks) {
            check.check(designs, targetVersion);
        }
    }

    public boolean addCheck(IDbDesignCheck check) { return checks.add(check); }
    public boolean hasChecks() { return this.checks != null && !this.checks.isEmpty(); }

    protected void checkNullDesigns(List<Database> designs, int targetVersion) {
        if (designs == null || designs.size() <= 0) {
            throw new IllegalArgumentException("List of Databases cannot be null or empty");
        }
    }

    protected void checkNonNegativeTargetVersion(List<Database> designs, int targetVersion) {
        if (targetVersion < 0) {
            throw new IllegalArgumentException("Cannot target a negative DB version");
        }
    }

    protected void checkTargetVersionExists(List<Database> designs, int targetVersion) {
        Optional<Database> targetDesign = designs
            .stream()
            .filter((d) -> d.getVersion() == targetVersion)
            .findFirst();

        if (!targetDesign.isPresent()) {
            throw new IllegalArgumentException("Target version doesn't exist.");
        }
    }

    protected void checkAllNonNullScripts(List<Database> designs, int targetVersion) {
        // check that all designs includes non-null scripts.
        List<Database> db = designs
            .stream()
            .filter((d) -> d.getScript() == null || "".equals(d.getScript()))
            .collect(Collectors.toList());

        if (!db.isEmpty()) {
            throw new IllegalArgumentException("Cannot transition based on empty scripts.");
        }
    }

    protected void checkNoDuplicateVersions(List<Database> designs, int targetVersion) {
        // check for no duplicate versions.
        Set<Integer> set = designs.stream().map((d) -> d.getVersion()).collect(Collectors.toSet());

        if (set.size() != designs.size()) {
            throw new IllegalArgumentException("There are designs that share the same version number");
        }
    }

    protected void checkForDuplicateScripts(List<Database> designs, int targetVersion) {
        // check for duplicate scripts.
        Set<String> set = designs.stream().map((d) -> d.getScript().trim()).collect(Collectors.toSet());

        if (set.size() != designs.size()) {
            throw new IllegalArgumentException("There are designs with duplicate scripts");
        }
    }
}
