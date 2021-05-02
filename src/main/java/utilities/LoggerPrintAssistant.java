package utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerPrintAssistant {
    public static void printMessageWithSpecifiedThreadName(Logger logger, Level level, String message) {
        logger.log(level, Thread.currentThread().getName() + ": " + message);
    }

    public static void printMessageWithSpecifiedThreadName(Logger logger, Level level, String message, Throwable exception) {
        logger.log(level, Thread.currentThread().getName() + ": " + message, exception);
    }
}
