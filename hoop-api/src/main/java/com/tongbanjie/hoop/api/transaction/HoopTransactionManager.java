package com.tongbanjie.hoop.api.transaction;

import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopGlobal;

/**
 * Hoop事务管理器
 *
 * @author xu.qiang
 * @date 18/8/14
 */
public interface HoopTransactionManager {


    /**
     * 开启事务
     * @param hoopGlobal
     */
    HoopGlobal begin(HoopGlobal hoopGlobal);

    /**
     * 添加事务参与者
     * @param hoopBranch
     */
    void enlistParticipant(HoopBranch hoopBranch);

    /**
     * 提交事务:当try阶段明确成功
     * <p>
     * if(把事务参与者的commit方法都执行一遍){
     * 移除activity记录
     * }
     * 否则 恢复bean 做恢复
     * @param asyn
     */
    void commit(boolean asyn) throws Exception;

    void commit(HoopTransaction hoopTransaction) throws Exception;

    /**
     * 回滚事务:当try阶段明确失败
     * <p>
     * if(把事务参与者的rollback方法都执行一遍){
     * 移除activity记录
     * }
     * 否则 恢复bean 做恢复
     * @param asyn
     */
    void rollback(boolean asyn) throws Exception;

    void rollback(HoopTransaction hoopTransaction) throws Exception;


    /**
     * 当前事务是否有效
     */
    boolean isActive();

    /**
     * 获取当前事务对象
     */
    HoopTransaction getCurrentTransaction();

    /**
     * 事务完成后clean
     * @param transaction
     */
    void cleanAfterCompletion(HoopTransaction transaction);


    /**
     * 提交补偿事务
     */
    void commitCompensate();


}
