package li.winston.cateserver.util;

import li.winston.cateserver.CateServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

/**
 * Created by winston on 19/01/2016.
 */
public class Log {

    static {
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss:SSS Z");
    }

    private static Logger logger = LoggerFactory.getLogger(CateServer.class);

    public static void trace(String msg) {
        logger.trace(msg);
    }

    public static void trace(String msg, Throwable t) {
        logger.trace(msg, t);
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }

    public static void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void info(String format, Object arg) {
        logger.info(format, arg);
    }

    public static void info(String format, Object arg1, Object arg2) {
        logger.info(format, arg1, arg2);
    }

    public static void info(String format, Object... args) {
        logger.info(format, args);
    }

    public static void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }

    public static void warn(String msg, Object arg) {
        logger.warn(msg, arg);
    }

    public static void warn(String msg, Object arg1, Object arg2) {
        logger.warn(msg, arg1, arg2);
    }

    public static void warn(String msg, Object... args) {
        logger.warn(msg, args);
    }

    public static void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

}
