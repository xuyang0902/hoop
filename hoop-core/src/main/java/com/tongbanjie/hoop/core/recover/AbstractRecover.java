package com.tongbanjie.hoop.core.recover;

import com.tongbanjie.hoop.api.config.HoopClientConfig;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.recovery.HoopRecover;
import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.core.utils.HoopLogger;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tcc事务恢复执行器
 *
 * @author xu.qiang
 * @date 18/8/22
 */
public abstract class AbstractRecover implements HoopRecover {


    private AtomicBoolean lock = new AtomicBoolean(true);

    private HoopClientConfig hoopClientConfig;

    protected ThreadPoolExecutor threadPool = null;

    protected HoopTransactionManager transactionManager;

    protected TransactionRepositry transactionRepositry;

    public AbstractRecover(HoopClientConfig hoopClientConfig, HoopTransactionManager transactionManager, TransactionRepositry transactionRepositry) {
        this.hoopClientConfig = hoopClientConfig;
        this.transactionManager = transactionManager;
        this.transactionRepositry = transactionRepositry;
    }

    @Override
    public void handleHoopGlobals(String hoopType) {

        if (!lock.compareAndSet(true, false)) {
            HoopLogger.warn(">> last runRecoverAll still，running refuse ");
            return;
        }

        HoopLogger.info(">> hoopType:{} Recover started", hoopType);

        while (true) {

            try {

                HoopLogger.info(">> hoopType:{} Recover loop begin", hoopType);

                String startTsId = null;
                while (true) {

                    List<HoopGlobal> hoopGlobalList = transactionRepositry.getNeedRecoverGlobalInfos(startTsId, hoopType, hoopClientConfig);
                    if (CollectionUtils.isEmpty(hoopGlobalList)) {
                        break;
                    }

                    for (final HoopGlobal global : hoopGlobalList) {

                        Date now = new Date();
                        Date timeOutPoint = new Date(global.getGmtModify().getTime() + hoopClientConfig.getBeforTime() * 1000L * 60);
                        if (now.compareTo(timeOutPoint) >= 0) {
                            /*
                             * 框架默认是单词100条补偿数据，3分钟应该妥妥处理完毕了，所以这里不break，log waning吧
                             * 超时机制并不能保证二阶段完全没有并发，只是尽最大可能的去减少二阶段的并发，所有两个CC操作还是要保证幂等调用
                             */
                            HoopLogger.warn(">> get补偿lock后，拿到数据处理过慢超时，放弃后续操作,tsId:{}", global.getTsId());
                            continue;
                        }

                        Integer recoverCount = global.getRecoverCount();
                        if (recoverCount > hoopClientConfig.getMaxRecoverCount()) {
                            HoopLogger.warn(">> Hoop tsId:{} 恢复超过次数 recoverCount:{} ", global.getTsId(), recoverCount);
                            return;
                        }

                        threadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    handleHoopGlobal(global);
                                } catch (Exception e) {
                                    HoopLogger.error(">> AbstractRecover handleHoopGlobal ,tsId:{} , tsType:{},", global.getTsId(), global.getTsType());
                                    HoopLogger.error("", e);
                                }
                            }
                        });

                    }

                    startTsId = hoopGlobalList.get(hoopGlobalList.size() - 1).getTsId();
                }

                HoopLogger.info(">> hoopType:{} Recover loop end", hoopType);

                //每隔3分钟补偿一次
                Thread.sleep(60 * 1000 * hoopClientConfig.getRecoverTimeInterval());

            } catch (Exception e) {
                HoopLogger.error(">> AbstractRecover error >>> ", e);
            }
        }
    }


    /**
     * 处理事务单据
     * @param hoopGlobal
     */
    protected abstract void handleHoopGlobal(HoopGlobal hoopGlobal) throws Exception;



    public TransactionRepositry getTransactionRepositry() {
        return transactionRepositry;
    }

    public void setTransactionRepositry(TransactionRepositry transactionRepositry) {
        this.transactionRepositry = transactionRepositry;
    }

    public HoopClientConfig getHoopClientConfig() {
        return hoopClientConfig;
    }

    public void setHoopClientConfig(HoopClientConfig hoopClientConfig) {
        this.hoopClientConfig = hoopClientConfig;
    }
}
