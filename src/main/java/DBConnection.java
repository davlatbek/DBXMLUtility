import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class for connecting to database, using singleton pattern
 * <p>
 *
 * @author  davlet
 * @version 1.0
 * @since   5/5/17
 */
public class DBConnection {
    public static DBConnection db = null;
    Connection connection = null;

    private DBConnection(){

    }

    /**
     * Gets instance of database connection, if not, creates new
     * @return DBConnection
     */
    public static synchronized DBConnection getInstance(){
        if (db == null){
            db = new DBConnection();
        }
        return db;
    }

    /**
     * Sets connection to database according to properties file settings
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
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

    /**
     * Shows if connection to database is still opened
     * @return <tt>true</tt> if connection is still valid
     * @throws SQLException
     */
    private boolean isConnected() throws SQLException {
        return !connection.isClosed();
    }
}