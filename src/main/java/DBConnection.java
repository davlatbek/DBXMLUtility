import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by davlet on 5/5/17.
 */
public class DBConnection {
    public static DBConnection db = null;
    Connection connection = null;

    private DBConnection(){

    }

    public static synchronized DBConnection getInstance(){
        if (db == null){
            db = new DBConnection();
        }
        return db;
    }

    protected void setConnection() throws SQLException, IOException, ClassNotFoundException {
        Properties dbProperties = new Properties();
//        InputStream in = new FileInputStream("../settings.properties");
        InputStream in = new FileInputStream("settings.properties");
        dbProperties.load(in);

        Class.forName(dbProperties.getProperty("jdbc.driverClassName"));
        String url = dbProperties.getProperty("jdbc.url");
        String user = dbProperties.getProperty("jdbc.username");
        String password = dbProperties.getProperty("jdbc.password");
        connection = DriverManager.getConnection(url, user, password);

    }

    private boolean isConnected() throws SQLException {
        return !connection.isClosed();
    }
}