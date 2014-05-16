package XplorJavaDB;

import java.util.List;

public interface IPreTransitionCheck {

    /**
     * Runs the stored checks against the provided design set and targetVersion.
     * Each design check can possibly throw an exception to fail a given
     * check.
     *
     * @param designs Iterative design applications.
     * @param targetVersion The design target (or last) design to run in the series.
     */
    public void runChecks(List<Database> designs, int targetVersion);
}
