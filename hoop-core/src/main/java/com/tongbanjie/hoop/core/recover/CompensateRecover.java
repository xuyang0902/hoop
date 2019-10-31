package com.tongbanjie.hoop.core.recover;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.recovery.TransactionHook;
import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;
import com.tongbanjie.hoop.api.threadfactory.HoopThreadFactory;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.core.utils.HoopLogger;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;
import org.springframework.util.Assert;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 补偿事务恢复执行器
 *
 * @author xu.qiang
 * @date 18/8/22
 */
public class CompensateRecover extends AbstractRecover implements Runnable {

    public CompensateRecover(HoopClientConfig hoopClientConfig, HoopTransactionManager transactionManager, TransactionRepositry transactionRepositry) {
        super(hoopClientConfig, transactionManager, transactionRepositry);

        threadPool = new ThreadPoolExecutor(1, 4, 10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(500),
                new HoopThreadFactory("Hoop-CompensateRecover-"),
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
    public void handleHoopGlobal(HoopGlobal hoopGlobal) {
        if (hoopGlobal == null) {
            return;
        }

        String state = hoopGlobal.getState();
        String tsId = hoopGlobal.getTsId();

        //1、有可能上一个线程处理完了，那么直接不用处理了
        hoopGlobal = transactionRepositry.selectGlobalById(tsId);
        if (hoopGlobal == null) {
            return;
        }

        hoopGlobal.setRecoverCount(hoopGlobal.getRecoverCount() + 1);
        transactionRepositry.updateGlobalLog(hoopGlobal);

        HoopLogger.info(">> Hoop Compensate tsId:{} 开始恢复 state:{} ", tsId, state);

        Class resolverBean = hoopGlobal.getResolverBean();
        Assert.notNull(resolverBean, "补偿事务 补偿bean必须存在 tdId:" + tsId);

        Object bean = SpringContextHolder.getBean(resolverBean);
        if (bean == null || !(bean instanceof TransactionHook)) {
            throw new RuntimeException("检查global的recoverBean是否配置正确，beanName：" + resolverBean.getSimpleName());
        }

        String hoopGlobalState = ((TransactionHook) bean).hook(hoopGlobal).getCode();
        if (GlobalState.COMMIT.getCode().equals(hoopGlobalState) || GlobalState.ROLLBACK.getCode().equals(hoopGlobalState)) {
            HoopLogger.info(">> Hoop Compensate tsId:{}  事务终态移除事务单据", tsId, hoopGlobalState);

            //事务完成
            hoopGlobal.setState(GlobalState.FINISH.getCode());
            transactionRepositry.updateGlobalLog(hoopGlobal);

            //移除事务单据
            transactionRepositry.removeGlobal(tsId);
        } else {
            HoopLogger.info(">> Hoop Compensate tsId:{}  恢复结果未知，returnState:{},等待下一次恢复", tsId, hoopGlobalState);
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

        handleHoopGlobals(HoopType.COMPENSATE.getCode());

    }

}
