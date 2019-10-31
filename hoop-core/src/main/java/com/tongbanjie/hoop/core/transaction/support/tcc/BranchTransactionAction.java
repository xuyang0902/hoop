package com.tongbanjie.hoop.core.transaction.support.tcc;

import com.tongbanjie.hoop.api.exception.RollbackException;
import com.tongbanjie.hoop.api.exception.SuspendException;
import com.tongbanjie.hoop.core.transaction.support.model.BranchTState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分支事务回调方法
 *
 * @author xu.qiang
 * @date 18/10/30
 */
public abstract class BranchTransactionAction<Param, Response> {

    private static final Logger logger = LoggerFactory.getLogger(BranchTransactionAction.class);

    /**
     * 默认的策略是doTry如果执行异常，直接选择挂起
     *
     * @param t
     * @return
     */
    public Response execute(Param t, BranchTState branchTState) {

        Response r = this.doTry(t, branchTState);
        if (branchTState.getStatus() == null) {
            //分支事务正常执行完，且分支事务没有指定分支事务的状态，默认没有异常，为可提交
            branchTState.setCommit();
        } else if (branchTState.getStatus() == BranchTState.PROCESS || branchTState.getStatus() == BranchTState.UNKNOW) {
            throw new SuspendException("branch execute 状态未知|处理中 需要挂起");
        } else if (branchTState.getStatus() == BranchTState.FAILED) {
            throw new RollbackException("branch execute 状态失败 需要回滚");
        }

        return r;
    }


    abstract public Response doTry(Param t, BranchTState branchTState);

    abstract public void doCommit(Param t);

    abstract public void doCancel(Param t);

    protected int order() {
        return Integer.MIN_VALUE;
    }

    protected String version() {
        return "1.0";
    }

    protected String app() {
        return "defualt_app";
    }

}
