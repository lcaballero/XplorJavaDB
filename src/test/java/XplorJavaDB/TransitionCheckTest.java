package XplorJavaDB;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class TransitionCheckTest extends Helpers {

    @Test
    public void addCheck_accepts_function() {
        IDbDesignCheck c = ((d, v) -> {});
        TransitionChecks a = new TransitionChecks();

        // Proves that underlying collection isn't null or at least
        // that no exceptions are thrown with this simple call.
        // Follow up tests will determine if the confirm is called, which
        // should imply some kind of non-null storage as well.
        a.addCheck(c);

        assertThat(a.hasChecks(), is(true));
    }

    @Test
    public void func_added_to_addCheck_is_ran() {
        TransitionChecks checks = new TransitionChecks();
        final boolean[] called = new boolean[] { false };  // stupid 'final' hack

        checks.addCheck((d, v) -> called[0] = true);
        checks.runChecks(dbVersionRange(0, 10), 7);

        assertThat(called[0], is(true));
    }

}
