package com.tongbanjie.hoop.core.transaction.support.compensate;

import com.tongbanjie.hoop.api.constants.Env;
import com.tongbanjie.hoop.api.enums.HoopType;
import com.tongbanjie.hoop.core.transaction.support.model.GlobalTstate;

/**
 * 补偿事务回调函数
 *
 * @author xu.qiang
 * @date 18/10/30
 */
public abstract class CompensateTransactionAction<Response> {

    /**
     * 主流程执行
     *
     * @param globalTstate
     * @return
     */
    abstract public Response doInTransaction(/*必须*/String tsId, GlobalTstate globalTstate);

    /**
     * 获取补偿事务钩子，事务异常补偿时候使用
     *
     * @return
     */
    abstract public Class getCompensateHook();

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
        return HoopType.COMPENSATE.getCode();
    }

    /**
     * 超时时间  默认6秒
     * @return
     */
    protected Integer timeOut(){
        return 6;
    }


}
