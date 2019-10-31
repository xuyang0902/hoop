package com.tongbanjie.hoop.core.transaction;

import com.tongbanjie.hoop.api.enums.BranchState;
import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopBranchInvocation;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.storage.plugins.repository.TransactionRepositry;
import com.tongbanjie.hoop.api.threadfactory.HoopThreadFactory;
import com.tongbanjie.hoop.api.transaction.HoopTransaction;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.api.utils.ActionNameUtil;
import com.tongbanjie.hoop.core.utils.HoopLogger;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hoop核心事务管理器
 *
 * @author xu.qiang
 * @date 18/8/14
 */
public class HoopCoreTransactionManager implements HoopTransactionManager {

    private TransactionRepositry transactionRepositry;

    private HoopCoreInvoker hoopCoreInvoker = new HoopCoreInvoker();

    private static ThreadPoolExecutor threadPool = null;

    static {
        threadPool = new ThreadPoolExecutor(1, 4, 10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(10000),
                new HoopThreadFactory("Hoop-2STEP-"),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            if (r instanceof Step2Task) {
                                Step2Task task = (Step2Task) r;
                                HoopLogger.warn(" HoopCoreTransactionManager Method[rejectedExecution] try put step2 Task， 放弃 tsId:{} commit:{}",
                                        task.getHoopTransaction().getHoopGlobal().getTsId(), task.isCommit());
                            } else {
                                HoopLogger.error(" HoopCoreTransactionManager Method[rejectedExecution] try put step2 Task，task is not Step2Task");
                            }
                        } catch (Exception e) {
                            HoopLogger.error(" HoopCoreTransactionManager Method[rejectedExecution] try put step2 Task! e:", e);
                        }
                    }
                });

        threadPool.allowCoreThreadTimeOut(true);
    }

    @Override
    public HoopGlobal begin(HoopGlobal hoopGlobal) {

        HoopLogger.debug(">> Hoop hoopType:{} tsId:{}  transaction begin ", hoopGlobal.getTsType(), hoopGlobal.getTsId());

        // 添加活动日志
        HoopGlobal hoopGlobalDb = transactionRepositry.addGlobalLog(hoopGlobal);

        // 注册事务
        HoopTransaction transaction = new HoopTransaction();
        transaction.setHoopGlobal(hoopGlobalDb);
        registerTransaction(transaction);

        return hoopGlobalDb;
    }

    @Override
    public void enlistParticipant(HoopBranch hoopBranch) {

        HoopTransaction currentTransaction = getCurrentTransaction();
        if (currentTransaction == null) {
            throw new RuntimeException("添加branch时，当前事务并未开启");
        }

        hoopBranch.setTsId(currentTransaction.getHoopGlobal().getTsId());
        transactionRepositry.addBranch(hoopBranch, currentTransaction.getHoopGlobal());

        //当前事务添加action
        currentTransaction.addParticipant(hoopBranch);

    }

    @Override
    public void commit(boolean asyn) throws Exception {

        HoopTransaction hoopTransaction = getCurrentTransactionThrowExceptionIfNull();

        if (asyn) {
            threadPool.execute(new Step2Task(true, hoopTransaction));
        } else {
            commit(hoopTransaction);
        }
    }

    @Override
    public void commit(HoopTransaction hoopTransaction) throws Exception {

        HoopLogger.debug(">> Hoop tsId:{} tcc transaction[commit] begin ", hoopTransaction.getHoopGlobal().getTsId());

        /*
         * 事务提交中
         */
        HoopGlobal hoopGlobal = hoopTransaction.getHoopGlobal();
        hoopGlobal.setState(GlobalState.COMMIT.getCode());
        transactionRepositry.updateGlobalLog(hoopGlobal);

        /*
         * 依次提交参与者二阶段操作
         */
        List<HoopBranchInvocation> commits = ActionNameUtil.getTwoPhaseInvocation(hoopTransaction.getParticipants());
        for (HoopBranchInvocation commit : commits) {

            HoopBranch hoopBranch = commit.getHoopBranch();

            HoopLogger.debug(">> Hoop tsId:{} branch开始提交[begin] action_id:{} ", hoopGlobal.getTsId(), hoopBranch.getBranchId());
            hoopCoreInvoker.invokeCommit(commit);
            HoopLogger.debug(">> Hoop tsId:{} branch开始提交[end] action_id:{} ", hoopGlobal.getTsId(), hoopBranch.getBranchId());

            /*
             * 内存更新action
             * db更新action
             */
            hoopBranch.setState(BranchState.FINISH.getCode());
            transactionRepositry.updateBranch(hoopBranch);
        }

        /*
         * 事务完成
         */
        hoopGlobal.setState(GlobalState.FINISH.getCode());
        transactionRepositry.updateGlobalLog(hoopGlobal);

        //删除global和branch
        transactionRepositry.removeTransaction(hoopGlobal.getTsId());
        HoopLogger.debug(">> Hoop tsId:{} tcc transaction[commit] end ", hoopTransaction.getHoopGlobal().getTsId());

    }


    @Override
    public void rollback(boolean asyn) throws Exception {

        HoopTransaction hoopTransaction = getCurrentTransactionThrowExceptionIfNull();

        if (asyn) {
            threadPool.execute(new Step2Task(false, hoopTransaction));
        } else {
            rollback(hoopTransaction);
        }
    }

    @Override
    public void rollback(HoopTransaction hoopTransaction) throws Exception {

        HoopLogger.debug(">> Hoop tsId:{} tcc transaction[rollback] begin ", hoopTransaction.getHoopGlobal().getTsId());

        /*
         * 事务回滚中
         */
        HoopGlobal hoopGlobal = hoopTransaction.getHoopGlobal();
        hoopGlobal.setState(GlobalState.ROLLBACK.getCode());
        transactionRepositry.updateGlobalLog(hoopGlobal);


        /*
         * 一次回滚action的二阶段操作
         */
        List<HoopBranchInvocation> rollbacks = ActionNameUtil.getTwoPhaseInvocation(hoopTransaction.getParticipants());
        for (HoopBranchInvocation rollback : rollbacks) {

            HoopBranch hoopBranch = rollback.getHoopBranch();

            HoopLogger.debug(">> Hoop tsId:{} branch开始回滚[begin] action_id:{} ", hoopGlobal.getTsId(), hoopBranch.getBranchId());
            hoopCoreInvoker.invokeRollback(rollback);
            HoopLogger.debug(">> Hoop tsId:{} branch开始回滚[end] action_id:{} ", hoopGlobal.getTsId(), hoopBranch.getBranchId());

            /*
             * 内存更新action
             * db更新action
             */
            hoopBranch.setState(BranchState.FINISH.getCode());
            transactionRepositry.updateBranch(hoopBranch);
        }

        //事务状态调整
        hoopGlobal.setState(GlobalState.FINISH.getCode());
        transactionRepositry.updateGlobalLog(hoopGlobal);

        //删除global和branch
        transactionRepositry.removeTransaction(hoopTransaction.getHoopGlobal().getTsId());
        HoopLogger.debug(">> Hoop tsId:{} tcc transaction[rollback] end ", hoopTransaction.getHoopGlobal().getTsId());

    }

    /**
     * 注册事务
     *
     * @param transaction
     */
    private void registerTransaction(HoopTransaction transaction) {

        HoopTransaction transactions = HoopSession.getHoopTransaction();
        if (transactions == null) {
            HoopSession.setCurrentHoopTransaction(transaction);
        }
    }

    /**
     * 获取当前事务
     *
     * @return
     */
    @Override
    public HoopTransaction getCurrentTransaction() {
        return HoopSession.getHoopTransaction();
    }


    /**
     * 获取当前事务对象 如果为空抛出异常
     *
     * @return
     */
    public static HoopTransaction getCurrentTransactionThrowExceptionIfNull() {
        HoopTransaction hoopTransaction = HoopSession.getHoopTransaction();
        if (hoopTransaction == null) {
            throw new RuntimeException("getCurrentTransactionThrowExceptionIfNull:当前事务并未开启");
        }

        return hoopTransaction;
    }

    @Override
    public boolean isActive() {
        HoopTransaction transactions = HoopSession.getHoopTransaction();
        return transactions != null;
    }


    @Override
    public void cleanAfterCompletion(HoopTransaction transaction) {
        if (transaction == null) {
            return;
        }

        HoopSession.removeAll();
    }

    @Override
    public void commitCompensate() {
        HoopTransaction hoopTransaction = getCurrentTransactionThrowExceptionIfNull();
        HoopLogger.debug(">> Hoop tsId:{} Compensate transaction[commit]", hoopTransaction.getHoopGlobal().getTsId());

        /*
         * 补偿事务完成，直接调整事务状态为完成 移除
         */
        HoopGlobal hoopGlobal = hoopTransaction.getHoopGlobal();
        hoopGlobal.setState(GlobalState.FINISH.getCode());
        transactionRepositry.updateGlobalLog(hoopGlobal);

        //移除补偿单据
        transactionRepositry.removeGlobal(hoopTransaction.getHoopGlobal().getTsId());
    }


    class Step2Task implements Runnable {

        private boolean commit;
        private HoopTransaction hoopTransaction;

        public Step2Task(boolean commit, HoopTransaction hoopTransaction) {
            this.commit = commit;
            this.hoopTransaction = hoopTransaction;
        }

        @Override
        public void run() {
            if (commit) {
                try {
                    commit(hoopTransaction);
                } catch (Exception e) {
                    HoopLogger.error(">> Hoop tsId:{} tcc transaction[asyn commit] error ", hoopTransaction.getHoopGlobal().getTsId(), e);
                }
            } else {
                try {
                    rollback(hoopTransaction);
                } catch (Exception e) {
                    HoopLogger.error(">> Hoop tsId:{} tcc transaction[asyn rollback] error ", hoopTransaction.getHoopGlobal().getTsId(), e);
                }
            }
        }

        public boolean isCommit() {
            return commit;
        }

        public void setCommit(boolean commit) {
            this.commit = commit;
        }

        public HoopTransaction getHoopTransaction() {
            return hoopTransaction;
        }

        public void setHoopTransaction(HoopTransaction hoopTransaction) {
            this.hoopTransaction = hoopTransaction;
        }
    }


    public TransactionRepositry getTransactionRepositry() {
        return transactionRepositry;
    }

    public void setTransactionRepositry(TransactionRepositry transactionRepositry) {
        this.transactionRepositry = transactionRepositry;
    }
}
