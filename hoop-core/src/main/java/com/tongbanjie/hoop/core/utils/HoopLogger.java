package com.tongbanjie.hoop.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xu.qiang
 * @date 18/4/23
 */
public class HoopLogger {

    private static final Logger logger = LoggerFactory.getLogger("hoop");

    public static boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }


    public static void debug(String s) {
        if (isDebugEnabled()) {
            return;
        }
        logger.debug(s);
    }

    public static void debug(String s, Object o) {
        if (isDebugEnabled()) {
            return;
        }
        logger.debug(s, o);
    }

    public static void debug(String s, Object o, Object o1) {

        if (isDebugEnabled()) {
            return;
        }
        logger.debug(s, o, o1);
    }

    public static void info(String s) {
        logger.info(s);
    }

    public static void info(String s, Object o) {
        logger.info(s, o);
    }

    public static void info(String s, Object o, Object o1) {
        logger.info(s, o, o1);
    }

    public static void info(String s, Object[] objects) {
        logger.info(s, objects);
    }

    public static void warn(String s) {
        logger.warn(s);
    }

    public static void warn(String s, Object o) {
        logger.warn(s, o);
    }

    public static void warn(String s, Object[] objects) {
        logger.warn(s, objects);
    }

    public static void warn(String s, Object o, Object o1) {
        logger.warn(s, o, o1);
    }

    public static void error(String s) {
        logger.error(s);
    }


    public static void error(String s, Object o) {
        logger.error(s, o);
    }

    public static void error(String s, Object o, Object o1) {
        logger.error(s, o, o1);
    }

    public static void error(String s, Object[] objects) {
        logger.error(s, objects);
    }


}
