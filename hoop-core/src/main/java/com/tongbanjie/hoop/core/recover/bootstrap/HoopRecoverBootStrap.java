package com.tongbanjie.hoop.core.recover.bootstrap;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.core.recover.CompensateRecover;
import com.tongbanjie.hoop.core.recover.TccRecover;
import com.tongbanjie.hoop.core.utils.HoopLogger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * hoop恢复器 启动类
 *
 * @author xu.qiang
 * @date 18/8/30
 */
public class HoopRecoverBootStrap {

    private HoopClientConfig hoopClientConfig;

    private TransactionRepositry transactionRepositry;

    private HoopTransactionManager transactionManager;

    private AtomicBoolean start = new AtomicBoolean(false);

    public void init() {

        if (start.get()) {
            HoopLogger.warn(">> HoopRecoverBootStrap is already started");
            return;
        }

        start.compareAndSet(false, true);

        HoopLogger.info(">> HoopRecoverBootStrap start ...");

        // hoopLock数据初始化
        transactionRepositry.initHoopLock(hoopClientConfig.getAppName());

        TccRecover tccRecover = new TccRecover(hoopClientConfig, transactionManager, transactionRepositry);
        CompensateRecover compensateRecover = new CompensateRecover(hoopClientConfig, transactionManager, transactionRepositry);

        //tcc恢复线程
        Thread tccThread = new Thread(tccRecover);
        tccThread.setName("TccRecover");
        tccThread.setDaemon(true);
        tccThread.start();

        //补偿恢复线程
        Thread compensateThread = new Thread(compensateRecover);
        compensateThread.setName("CompensateRecover");
        compensateThread.setDaemon(true);
        compensateThread.start();

        HoopLogger.info(">> HoopRecoverBootStrap started ...");
    }

    public HoopClientConfig getHoopClientConfig() {
        return hoopClientConfig;
    }

    public void setHoopClientConfig(HoopClientConfig hoopClientConfig) {
        this.hoopClientConfig = hoopClientConfig;
    }

    public TransactionRepositry getTransactionRepositry() {
        return transactionRepositry;
    }

    public void setTransactionRepositry(TransactionRepositry transactionRepositry) {
        this.transactionRepositry = transactionRepositry;
    }

    public HoopTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(HoopTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
