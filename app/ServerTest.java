import java.sql.*;
import java.util.Arrays;

public class DBcon {
    public Connection con;
    public Statement stmt;
    
    public DBcon() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mariadb://host:3306/table",
                    "user",
                    "password");
            stmt = con.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException
    {
        return stmt.executeQuery(sql);
    }

    public int executeSQL(String sql) throws SQLException {
        return stmt.executeUpdate(sql);
    }

    public void execute(String sql) throws SQLException {
        stmt.execute(sql);
    }
}
