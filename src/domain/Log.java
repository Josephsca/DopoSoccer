package domain;    

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

/**
 * Handles error logging for developers.
 * Each error is recorded in the DopoSoccer.log file inside the project folder.
 */
public class Log{
    public static final String NAME ="DopoSoccer";

    /**
     * Records the details of an error into the log file.
     * @param e the exception error to be logged
     */
    public static void record(Exception e){
        try{
            Logger logger=Logger.getLogger(NAME);
            logger.setUseParentHandlers(false);
            FileHandler file=new FileHandler(NAME+".log",true);
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            logger.log(Level.SEVERE,e.toString(),e);
            file.close();
        }catch (Exception oe){
            System.err.println("No se pudo escribir el log: " + oe.getMessage());
        }
    }
}