package com.tongbanjie.hoop.core.transaction.support.tcc;

import com.tongbanjie.hoop.api.enums.BranchState;
import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.exception.HoopException;
import com.tongbanjie.hoop.api.exception.RollbackException;
import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopContext;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.recovery.TransactionHook;
import com.tongbanjie.hoop.api.transaction.HoopTransaction;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.api.utils.UUIDUtil;
import com.tongbanjie.hoop.core.transaction.HoopSession;
import com.tongbanjie.hoop.core.transaction.support.model.BranchTState;
import com.tongbanjie.hoop.core.transaction.support.model.GlobalTstate;
import com.tongbanjie.hoop.core.utils.HoopLogger;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * tcc事务模板
 *
 * @author xu.qiang
 * @date 18/10/30
 */
public class TccTransactionTemplate {

    @Autowired
    private HoopTransactionManager transactionManager;


    /**
     * 执行全局事务
     *
     * @param tsId       全局事务id 业务方保证幂等
     * @param action     主流程组合流程
     * @param <Response>
     * @return
     * @throws Exception
     */
    public <Response> Response executeGlobal(String tsId, GlobalTransactionAction<Response> action) throws Exception {

        /*
         * 00.检查下事务状态查询器
         */
        Class globalStateConfirmClazz = action.getGlobalStateConfirmClazz();

        Object confirmClazz = SpringContextHolder.getBean(globalStateConfirmClazz);

        if (confirmClazz != null && !(confirmClazz instanceof TransactionHook)) {
            throw new RuntimeException("confirmClazz not implements GlobalTransactionStateConfirm，recoveryBeanName:" + confirmClazz.getClass().getName());
        }

        if (transactionManager.isActive()) {

            /**
             * hoop不支持多个activity一起开启的模式。
             */
            throw new HoopException("hoop not support multiple activity ");

        }

        Response returnValue = null;
        boolean asyn = action.asynHanldeC();

        try {

            if (tsId == null) {
                throw new RuntimeException("开启活动日志，tsId必须非空");
            }

            HoopGlobal hoopGlobal = HoopGlobal.build(tsId,
                    new HoopContext(),
                    globalStateConfirmClazz,
                    action.env(),
                    action.hoopType(),
                    action.timeOut());
            hoopGlobal = transactionManager.begin(hoopGlobal);//开启事务

            GlobalTstate globalTstate = new GlobalTstate();
            try {

                returnValue = action.doInTransaction(tsId, globalTstate);

            } catch (Exception tryException) {

                if (mustRollbackFor(tryException)) {
                    HoopLogger.error(">> Hoop tsId:{} try阶段明确失败 直接回滚 e:", tsId, tryException);
                    transactionManager.rollback(asyn);
                }

                throw tryException;
            }

            Integer status = globalTstate.getStatus();
            if (status == null || status == GlobalTstate.SUCC) {
                transactionManager.commit(asyn);
            } else if (status == GlobalTstate.FAILED) {
                transactionManager.rollback(asyn);
            } else {
                //unknow process
            }

            HoopLogger.info(">> Hoop tsId:{} 事务执行时间:{}ms ", tsId, System.currentTimeMillis() - hoopGlobal.getStartTime().getTime());

            return returnValue;

        } finally {

            //当前线程移除transaction
            transactionManager.cleanAfterCompletion(HoopSession.getHoopTransaction());

        }
    }


    /**
     * 分支流程事务处理
     * 必须在executeGlobal中执行
     *
     * @param param
     * @param action
     * @param <Param>
     * @param <Response>
     * @return
     */
    public <Param, Response> Response executeBranch(Param param, BranchTransactionAction<Param, Response> action/*必须是一个spring的实例 二阶段需要用*/) {
        HoopTransaction transaction = transactionManager.getCurrentTransaction();

        if (transaction != null && transaction.getHoopGlobal().getState().equals(GlobalState.INIT.getCode())) {
            enlistParticipant(param, action, transaction);
        }

        return action.execute(param, new BranchTState());
    }


    /**
     * 判断该异常是否需要延迟操作第二步操作
     *
     * @param throwable
     * @return
     */
    private boolean mustRollbackFor(Throwable throwable) {

        Throwable rootCause = ExceptionUtils.getRootCause(throwable);
        if (RollbackException.class.isAssignableFrom(throwable.getClass())
                || (rootCause != null && RollbackException.class.isAssignableFrom(rootCause.getClass()))) {
            return true;
        }

        return false;
    }


    /**
     * 加入事务参与者
     *
     * @param param
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private <Param, Response> void enlistParticipant(Param param, BranchTransactionAction<Param, Response> action, HoopTransaction transaction) {

        String beanId = StringUtils.uncapitalize(action.getClass().getSimpleName());
        Object bean = SpringContextHolder.getBean(beanId);
        Assert.notNull(bean, "branch未设置二阶段的beanId，actionClass：" + beanId);

        //把分支事务开启的入参 序列化到db
        HoopContext hoopContext = new HoopContext();
        hoopContext.setArgs(new Object[]{param});
        hoopContext.setArgsTypes(new Class[]{param.getClass()});

        HoopBranch hoopBranch = new HoopBranch();
        hoopBranch.setContext(hoopContext);

        /*
         * 应用服务名称:beanId:try方法:commit方法:rollBack方法:二阶段顺序:版本号
         */
        StringBuilder sb = new StringBuilder();
        sb.append(action.app()).append(":");
        sb.append(beanId).append(":");
        sb.append("doTry").append(":");
        sb.append("doCommit").append(":");
        sb.append("doCancel").append(":");
        sb.append(action.order()).append(":");
        sb.append(action.version()).append(":");
        hoopBranch.setActionName(sb.toString());

        hoopBranch.setBranchId(UUIDUtil.getUUId());
        hoopBranch.setState(BranchState.INIT.getCode());

        transactionManager.enlistParticipant(hoopBranch);

    }


}
