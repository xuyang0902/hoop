package com.tongbanjie.hoop.api.model;

import com.tongbanjie.hoop.api.enums.BranchState;

import java.io.Serializable;
import java.util.Date;

/**
 * 分布式分支事务model
 *
 * @author xu.qiang
 * @date 18/8/7
 */
public class HoopBranch implements Serializable {

    private static final long serialVersionUID = -6895153779632706052L;

    /**
     * 分支事务ID 用UUID
     */
    private String branchId;

    /**
     * 事务ID
     */
    private String tsId;

    /**
     * 应用:beanId:try方法:commit方法:rollBack方法:二阶段顺序:版本号
     * <p>
     * 举例：
     * trade:combineBuyService:buy:commitBuy:rollBackBuy:-1:1.0
     */
    private String actionName;

    /**
     * I:初始，F:完成
     *
     * @see BranchState
     */
    private String state;

    /**
     * 一阶段方法参数json序列化
     */
    private HoopContext context;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;


    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getTsId() {
        return tsId;
    }

    public void setTsId(String tsId) {
        this.tsId = tsId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public HoopContext getContext() {
        return context;
    }

    public void setContext(HoopContext context) {
        this.context = context;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "HoopBranch{" +
                "branchId='" + branchId + '\'' +
                ", tsId='" + tsId + '\'' +
                ", actionName='" + actionName + '\'' +
                ", state='" + state + '\'' +
                ", context='" + context + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}
