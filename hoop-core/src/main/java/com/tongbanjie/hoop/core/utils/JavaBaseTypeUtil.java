package com.tongbanjie.hoop.core.utils;

/**
 * @author xu.qiang
 * @date 18/10/10
 */
public class JavaBaseTypeUtil {

    /**
     * 判断object是否为基本类型
     *
     * @param object
     * @return
     */
    public static boolean isBaseType(Object object) {
        Class className = object.getClass();
        if (className.equals(java.lang.Integer.class) ||
                className.equals(java.lang.Byte.class) ||
                className.equals(java.lang.Long.class) ||
                className.equals(java.lang.Double.class) ||
                className.equals(java.lang.Float.class) ||
                className.equals(java.lang.Character.class) ||
                className.equals(java.lang.Short.class) ||
                className.equals(java.lang.Boolean.class)) {
            return true;
        }
        return false;
    }


    /**
     * 判断aClass是否为基本类型
     *
     * @param aClass
     * @return
     */
    public static boolean isClazzBaseType(Class aClass) {
        if (aClass.equals(java.lang.Integer.class) ||
                aClass.equals(java.lang.Byte.class) ||
                aClass.equals(java.lang.Long.class) ||
                aClass.equals(java.lang.Double.class) ||
                aClass.equals(java.lang.Float.class) ||
                aClass.equals(java.lang.Character.class) ||
                aClass.equals(java.lang.Short.class) ||
                aClass.equals(java.lang.Boolean.class)) {
            return true;
        }
        return false;
    }

}
