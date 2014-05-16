package XplorJavaDB;

import java.util.List;

@FunctionalInterface
public interface IDbDesignCheck {
    public void confirm(List<Database> designs, int targetVersion);
}
