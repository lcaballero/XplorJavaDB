package XplorJavaDB;

import java.util.List;

@FunctionalInterface
public interface IDbDesignCheck {
    public void check(List<Database> designs, int targetVersion);
}
