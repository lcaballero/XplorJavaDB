package XplorJavaDB;


import java.sql.SQLException;

public class Main {

    public static void main( String[] args ) throws Exception {
        new ReadDb().insertBoard();
        new ReadDb().readBoards();
    }
}
