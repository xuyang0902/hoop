package com.tongbanjie.hoop.core.transaction.support.model;

/**
 * 分支事务执行状态
 *
 * @author xu.qiang
 * @date 18/10/30
 */
public class BranchTState {


    public static final int SUCC = 1;
    public static final int PROCESS = 0;
    public static final int FAILED = -1;
    public static final int UNKNOW = -2;

    private Integer status = null;

    public void setRollbackOnly() {
        this.status = FAILED;
    }

    public void setCommit() {
        this.status = SUCC;
    }

    public void setSuspend() {
        this.status = PROCESS;
    }

    public void setUnknow(){
        this.status = UNKNOW;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
