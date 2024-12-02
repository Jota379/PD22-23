package Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Utilsdb {

    public static int getVersionDB(Statement s){
        try {
            ResultSet rs = s.executeQuery("PRAGMA user_version");
            return rs.getInt("user_version");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void incrementVersionDB(Statement s){
        try {
            ResultSet rs = s.executeQuery("PRAGMA user_version");
            s.executeUpdate("PRAGMA user_version="+(rs.getInt("user_version")+1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
