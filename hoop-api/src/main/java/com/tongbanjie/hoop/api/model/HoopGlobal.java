package com.tongbanjie.hoop.api.model;

import com.tongbanjie.hoop.api.enums.GlobalState;
import com.tongbanjie.hoop.api.enums.HoopType;

import java.io.Serializable;
import java.util.Date;

/**
 * 分布式事务活动开始日志
 *
 * @author xu.qiang
 * @date 18/8/7
 */
public class HoopGlobal implements Serializable {

    private static final long serialVersionUID = 808686556413137077L;

    /**
     * 事务ID
     */
    private String tsId;

    /**
     * 事务类型
     *
     * @see HoopType
     */
    private String tsType;

    /**
     * action记数
     */
    private Integer actionCount;

    /**
     * 启动时间
     */
    private Date startTime;

    /**
     * 全局事务入参
     */
    private HoopContext context;

    /**
     * U:未知,I:初始,C:提交中,R:回滚中,F:完成
     *
     * @see GlobalState
     */
    private String state;

    /**
     * 恢复记数
     */
    private Integer recoverCount;

    /**
     * 超时，秒
     */
    private Integer timeout = 6;

    /**
     * 状态反查器,spring beanId
     */
    private Class resolverBean;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;

    /**
     * 运行环境
     */
    private String env;


    public static HoopGlobal build(String tsId, HoopContext context, Class resolverBean, String env, String tsType,Integer timeout) {
        HoopGlobal hoopGlobal = new HoopGlobal();
        hoopGlobal.setTsId(tsId);
        hoopGlobal.setTsType(tsType);
        hoopGlobal.setContext(context);
        hoopGlobal.setResolverBean(resolverBean);
        hoopGlobal.setEnv(env);
        hoopGlobal.setTimeout(timeout);
        return hoopGlobal;
    }

    public String getTsId() {
        return tsId;
    }

    public void setTsId(String tsId) {
        this.tsId = tsId;
    }

    public Integer getActionCount() {
        return actionCount;
    }

    public void setActionCount(Integer actionCount) {
        this.actionCount = actionCount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public HoopContext getContext() {
        return context;
    }

    public void setContext(HoopContext context) {
        this.context = context;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getRecoverCount() {
        return recoverCount;
    }

    public void setRecoverCount(Integer recoverCount) {
        this.recoverCount = recoverCount;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Class getResolverBean() {
        return resolverBean;
    }

    public void setResolverBean(Class resolverBean) {
        this.resolverBean = resolverBean;
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

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getTsType() {
        return tsType;
    }

    public void setTsType(String tsType) {
        this.tsType = tsType;
    }

}
