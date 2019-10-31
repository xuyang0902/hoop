package com.tongbanjie.hoop.core.transaction;

import com.tongbanjie.hoop.api.transaction.HoopTransaction;

/**
 * Hoop线程上线文
 *
 * @author xu.qiang
 * @date 18/8/22
 */
public class HoopSession {

    //事务session
    private final static ThreadLocal<HoopTransaction> transactionSession = new ThreadLocal<HoopTransaction>();

    public static HoopTransaction getHoopTransaction() {
        return transactionSession.get();
    }

    public static void setCurrentHoopTransaction(HoopTransaction hoopTransaction) {
        transactionSession.set(hoopTransaction);
    }

    public static void removeAll() {
        transactionSession.remove();
    }

}
