package com.tongbanjie.hoop.api.recovery;

import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.model.HoopGlobal;

/**
 * Hoop事务钩子
 *
 * tcc事务用于查询事务状态，推动事务走向
 *
 * 补偿事务 用作补偿操作
 *
 * @author xu.qiang
 * @date 18/8/7
 */
public interface TransactionHook {

    /**
     * 事务恢复状态查询器
     * <p>
     * 问:主流程还在处理【可能处于挂起状态】，但是恢复线程这个时候可能已经来查询本次activity一阶段的执行结果，有概率会导致二阶段的提交操作并发。
     * #如何处理？？
     * 1、commit和rollback保持幂等
     * 2、恢复的线程默认查询的是3分钟前创建的activity
     * <p>
     * 问:如果你想要在事务状态查询器中执行业务逻辑,需要业务方自己解决 可能和主流程并发的问题。
     *
     * @param hoopGlobal
     * @return 返回一阶段 业务执行的结果 【提交（COMMIT）|回滚（ROLLBACK）|依旧挂起（UNKOWN）】
     * @see GlobalState
     */
    GlobalState hook(HoopGlobal hoopGlobal);

}
