package XplorJavaDB;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Helpers {

    public String dropScript(String name) {
        return String.format("DROP TABLE IF EXISTS %s;", name);
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
