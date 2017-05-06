import org.apache.log4j.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by davlet on 5/5/17.
 */
public class MyLogger {
    private final static Logger logger;

    static {
        logger = Logger.getLogger("FileLogger");
    }

    public MyLogger(){

    }

    static synchronized Logger getInstance(){
        if (logger != null){
            return logger;
        }
        return null;
    }

    static void resetLoggerConfiguration(){
        Properties logProperties = new Properties();
        Logger.getRootLogger().getLoggerRepository().resetConfiguration();
        InputStream in = null;
        try {
            in = new FileInputStream("settings.properties");
            logProperties.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.getRootLogger().addAppender(createCustomAppender(logProperties.getProperty("logName"), logProperties.getProperty("logLocation")));
    }

    private static Appender createCustomAppender(String name, String location){
        FileAppender fileAppender = new FileAppender();
        fileAppender.setName("FileLogger");
        fileAppender.setFile(location+name);
        fileAppender.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fileAppender.setThreshold(Level.DEBUG);
        fileAppender.setAppend(true);
        fileAppender.activateOptions();
        return fileAppender;
    }

}
