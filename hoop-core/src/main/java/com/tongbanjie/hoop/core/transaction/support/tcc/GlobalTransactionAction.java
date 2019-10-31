package com.tongbanjie.hoop.core.transaction.support.tcc;

import com.tongbanjie.hoop.api.constants.Env;
import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.core.transaction.support.model.GlobalTstate;

/**
 * 全局事务回调函数
 *
 * @author xu.qiang
 * @date 18/10/30
 */
public abstract class GlobalTransactionAction<Response/*全局事务返回值*/> {

    /**
     * 主流程执行
     *
     * @param globalTstate
     * @return
     */
    abstract public Response doInTransaction(/*必须*/String tsId, GlobalTstate globalTstate);

    /**
     * 获取事务状态查询器
     *
     * @return
     */
    abstract public Class getGlobalStateConfirmClazz();

    /**
     * 二阶段是否需要异步提交
     *
     * @return
     */
    protected boolean asynHanldeC() {
        return false;
    }

    /**
     * 环境
     *
     * @return
     */
    protected String env() {
        return Env.PROD;
    }

    /**
     * 分布式事务模式
     *
     * @return
     */
    protected String hoopType() {
        return HoopType.TCC.getCode();
    }

    /**
     * 超时时间  默认6秒
     * @return
     */
    protected Integer timeOut(){
        return 6;
    }

}
