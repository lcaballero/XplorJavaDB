package XplorJavaDB;

import XplorJavaDB.pg.ColumnProperties;
import XplorJavaDB.pg.TableDescription;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class Helpers {

    protected String activeTable = null;
    protected DBI dbi = null;

    public void hasType(
        Optional<ColumnProperties> p,
        String columnName,
        String type,
        Function<ColumnProperties, Boolean>... mapper) {

        assertThat("No present ColumnProperties for " + type, p.isPresent(), is(true));
        assertThat(p.get().getTypeName(), is(type));
        assertThat(p.get().getColumnName(), is(columnName));

        Arrays.asList(mapper)
            .stream()
            .forEach((fn) -> assertThat(fn.apply(p.get()), is(true)));
    }

    public void hasType(String col, String type, Function<ColumnProperties, Boolean>... mapper) {
        hasType(getColumn(col), col, type, mapper);
    }

    public Optional<ColumnProperties> getColumn(String col) {
        TableDescription desc = new TableDescription(dbi);
        Optional<ColumnProperties> p = desc.getColumn(activeTable, col);
        return p;
    }

    public void dropTables(DBI dbi, String[] tables) {
        for (String table : tables) {
            execute(dbi, dropScript(table));
        }
    }

    public String dropScript(String name) {
        return String.format("DROP TABLE IF EXISTS %s;", name);
    }

    public void execute(DBI dbi, String sql) {
        try (Handle handle = dbi.open()) {
            handle.execute(sql);
        }
    }

    public List<Database> basicDesigns() {
        List<Database> designs = new ArrayList<>();
        designs.add(new Database(1, "CREATE TABLE something (id int)"));
        designs.add(new Database(2, "CREATE TABLE user_profiles (id int, email text)"));

        return designs;
    }

    public List<Database> dbVersionRange(int from, int to) {
        return IntStream.range(from, to)
            .mapToObj((n) -> {
                Database db = new Database();
                db.setVersion(n);
                db.setScript("SELECT " + n);
                return db;
            })
            .collect(Collectors.toList());
    }
}
