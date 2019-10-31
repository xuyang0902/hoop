package com.tongbanjie.hoop.core.recover;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.recovery.TransactionHook;
import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;
import com.tongbanjie.hoop.api.threadfactory.HoopThreadFactory;
import com.tongbanjie.hoop.api.transaction.HoopTransaction;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.core.utils.HoopLogger;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Tcc事务恢复执行器
 *
 * @author xu.qiang
 * @date 18/8/22
 */
public class TccRecover extends AbstractRecover implements Runnable {

    public TccRecover(HoopClientConfig hoopClientConfig, HoopTransactionManager transactionManager, TransactionRepositry transactionRepositry) {
        super(hoopClientConfig, transactionManager, transactionRepositry);
        threadPool = new ThreadPoolExecutor(1, 4, 10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(500),
                new HoopThreadFactory("Hoop-TccRecover-"),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            executor.getQueue().put(r);
                        } catch (InterruptedException e) {
                            HoopLogger.error(" TccRecover Method[rejectedExecution] try put task to queue! occurs error = [" + e.getMessage() + "]", e);
                        }
                    }
                });
        threadPool.allowCoreThreadTimeOut(true);
    }

    @Override
    public void handleHoopGlobal(HoopGlobal hoopGlobal) throws Exception {

        if (hoopGlobal == null) {
            return;
        }

        String state = hoopGlobal.getState();
        String tsId = hoopGlobal.getTsId();

        //有可能上一个线程处理完了，那么直接不用处理了
        hoopGlobal = transactionRepositry.selectGlobalById(tsId);
        if (hoopGlobal == null) {
            return;
        }

        hoopGlobal.setRecoverCount(hoopGlobal.getRecoverCount() + 1);
        transactionRepositry.updateGlobalLog(hoopGlobal);

        HoopLogger.info(">> Hoop Tcc tsId:{} 开始恢复 state:{} ", tsId, state);

        if (GlobalState.FINISH.getCode().equals(state)) {
            transactionRepositry.removeTransaction(hoopGlobal.getTsId());
            return;
        }

        HoopTransaction hoopTransaction = new HoopTransaction();
        hoopTransaction.setHoopGlobal(hoopGlobal);
        hoopTransaction.setParticipants(transactionRepositry.selectBranchByTsId(tsId));

        if (GlobalState.COMMIT.getCode().equals(state)) {
            transactionManager.commit(hoopTransaction);
            return;
        }

        if (GlobalState.ROLLBACK.getCode().equals(state)) {
            transactionManager.rollback(hoopTransaction);
            return;
        }

        Class resolverBean = hoopGlobal.getResolverBean();
        if (resolverBean == null) {
            transactionManager.rollback(hoopTransaction);
            return;
        }

        Object bean = SpringContextHolder.getBean(resolverBean);
        if (bean == null || !(bean instanceof TransactionHook)) {
            throw new RuntimeException("检查global的recoverBean是否配置正确，beanName：" + resolverBean.getSimpleName() +
                    "tsId:" + tsId);
        }

        String hoopGlobalState = ((TransactionHook) bean).hook(hoopGlobal).getCode();
        if (GlobalState.COMMIT.getCode().equals(hoopGlobalState)) {
            transactionManager.commit(hoopTransaction);
        } else if (GlobalState.ROLLBACK.getCode().equals(hoopGlobalState)) {
            transactionManager.rollback(hoopTransaction);
        } else {
            HoopLogger.info(">> Hoop Tcc tsId:{}  恢复结果未知，returnState:{},等待下一次恢复", tsId, hoopGlobalState);
        }
    }

    @Override
    public void run() {

        Long random = (long) (Math.random() * super.getHoopClientConfig().getBeforTime() * 1000L * 60);

        try {
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        handleHoopGlobals(HoopType.TCC.getCode());
    }
}
