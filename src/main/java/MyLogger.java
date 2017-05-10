import org.apache.log4j.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class represents customised Logger class
 */
public class MyLogger {
    /**
     * Logger variable
     */
    private final static Logger logger;

    /**
     * Initialize logger statically
     */
    static {
        logger = Logger.getLogger("FileLogger");
    }

    /**
     * Gets instance of my logger
     * @return Logger
     */
    static synchronized Logger getInstance(){
        if (logger != null){
            return logger;
        }
        return null;
    }

    /**
     * Resets configuration of standard Logger
     */
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

    /**
     * Creates a custom appender for MyLogger
     * @param name name of log file
     * @param location path to log file
     * @return Appender
     */
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
