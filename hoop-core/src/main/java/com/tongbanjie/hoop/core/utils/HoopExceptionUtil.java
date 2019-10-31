package com.tongbanjie.hoop.core.utils;

import com.tongbanjie.hoop.api.annotation.GlobalT;
import com.tongbanjie.hoop.api.exception.RollbackException;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * hoop异常工具类
 * @author xu.qiang
 * @date 19/3/26
 */
public class HoopExceptionUtil {

    /**
     * 判断该异常是否需要延迟操作第二步操作
     *
     * @return
     */
    public static  boolean needRollbackFor(Throwable throwable, GlobalT globalT) {

        Class<? extends Throwable>[] clazz = globalT.rollbackFor();
        if (clazz == null || clazz.length == 0) {
            return false;
        }

        for (Class rollbackFor : clazz) {
            Throwable rootCause = ExceptionUtils.getRootCause(throwable);
            if (rollbackFor.isAssignableFrom(throwable.getClass())
                    || (rootCause != null && rollbackFor.isAssignableFrom(rootCause.getClass()))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断该异常是否需要延迟操作第二步操作
     *
     * @return
     */
    public static boolean mustRollbackFor(Throwable throwable) {

        Throwable rootCause = ExceptionUtils.getRootCause(throwable);
        if (RollbackException.class.isAssignableFrom(throwable.getClass())
                || (rootCause != null && RollbackException.class.isAssignableFrom(rootCause.getClass()))) {
            return true;
        }

        return false;
    }


}
