package XplorJavaDB;

import com.google.common.base.Joiner;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class AppTest
{
    @Test
    public void should_have_guave() {
        String hw = "Hello, World!";
        String joined = Joiner.on(", ").join("Hello", "World!");

        assertTrue(joined.equals(hw));
    }

    class ShouldHaveGuice extends AbstractModule {
        @Override
        protected void configure() {
        }
    }

    @Test
    public void should_create_app_instance() {
        Injector inject = Guice.createInjector(new ShouldHaveGuice());
        App app = inject.getInstance(App.class);
        assertThat(app, notNullValue());
    }

    @Test
    public void should_maintain_app_as_singleton() {
        Injector inject = Guice.createInjector(new ShouldHaveGuice());
        App app1 = inject.getInstance(App.class);
        App app2 = inject.getInstance(App.class);

        assertThat(app1, is(app2));
    }
}
