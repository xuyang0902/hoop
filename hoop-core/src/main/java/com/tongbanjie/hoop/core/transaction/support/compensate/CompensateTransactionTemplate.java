package com.tongbanjie.hoop.core.transaction.support.compensate;

import com.tongbanjie.hoop.api.model.HoopContext;
import com.tongbanjie.hoop.api.model.HoopGlobal;
import com.tongbanjie.hoop.api.recovery.TransactionHook;
import com.tongbanjie.hoop.api.transaction.HoopTransactionManager;
import com.tongbanjie.hoop.core.transaction.HoopSession;
import com.tongbanjie.hoop.core.transaction.support.model.GlobalTstate;
import com.tongbanjie.hoop.core.utils.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 补偿逻辑的template
 *
 * @author xu.qiang
 * @date 18/10/30
 */
public class CompensateTransactionTemplate {

    @Autowired
    private HoopTransactionManager transactionManager;


    /**
     * 补偿
     *
     * @param tsId       全局事务id 保证唯一
     * @param action     主流程处理 没有异常抛出默认主流程事务 可以自己设定事务状态
     * @param <Response>
     * @return
     */
    public <Response> Response execute(String tsId, CompensateTransactionAction<Response> action) {

        Class compensateHookClazz = action.getCompensateHook();
        Object compensateHook = SpringContextHolder.getBean(compensateHookClazz);
        if (compensateHook != null && !(compensateHook instanceof TransactionHook)) {
            throw new RuntimeException("compensateHookClazz not implements GlobalTransactionStateConfirm，recoveryBeanName:" + compensateHook.getClass().getName());
        }

        Response returnValue = null;
        try {
            if (tsId == null) {
                throw new RuntimeException("开启活动日志，tsId必须非空");
            }

            HoopGlobal hoopGlobal = HoopGlobal.build(tsId, new HoopContext(), compensateHookClazz,
                    action.env(), action.hoopType(), action.timeOut());
            transactionManager.begin(hoopGlobal);//开启事务

            GlobalTstate globalTstate = new GlobalTstate();

            //主流程
            returnValue = action.doInTransaction(tsId, globalTstate);

            Integer status = globalTstate.getStatus();
            if (status == null || status == GlobalTstate.SUCC || status == GlobalTstate.FAILED) {
                //主流程没有异常，明确成功or失败 提交事务
                transactionManager.commitCompensate();
            } else {
                //unknow process
            }

            return returnValue;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //当前线程移除transaction
            transactionManager.cleanAfterCompletion(HoopSession.getHoopTransaction());
        }
    }

}
