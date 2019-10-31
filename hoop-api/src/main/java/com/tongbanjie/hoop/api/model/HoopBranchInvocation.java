package com.tongbanjie.hoop.api.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 调用内容
 *
 * @author xu.qiang
 * @date 18/8/16
 */
public class HoopBranchInvocation implements Serializable {

    private static final long serialVersionUID = 2585862760266648223L;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * action执行的beanId
     */
    private String beanId;

    /**
     * try方法
     */
    private String tryMethodName;

    /**
     * commit方法
     */
    private String commitMethodName;

    /**
     * rollback方法
     */
    private String rollbackMethoName;

    /**
     * 参数值
     */
    private Object[] param;

    /**
     * 参数类型
     */
    private Class[] parameterTypes;

    /**
     * 版本号
     */
    private String version;

    /**
     * 二阶段执行顺序
     */
    private Long order;

    /**
     * 内存和db需要操作action对象
     */
    private HoopBranch hoopBranch;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getTryMethodName() {
        return tryMethodName;
    }

    public void setTryMethodName(String tryMethodName) {
        this.tryMethodName = tryMethodName;
    }

    public String getCommitMethodName() {
        return commitMethodName;
    }

    public void setCommitMethodName(String commitMethodName) {
        this.commitMethodName = commitMethodName;
    }

    public String getRollbackMethoName() {
        return rollbackMethoName;
    }

    public void setRollbackMethoName(String rollbackMethoName) {
        this.rollbackMethoName = rollbackMethoName;
    }

    public Object[] getParam() {
        return param;
    }

    public void setParam(Object[] param) {
        this.param = param;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public HoopBranch getHoopBranch() {
        return hoopBranch;
    }

    public void setHoopBranch(HoopBranch hoopBranch) {
        this.hoopBranch = hoopBranch;
    }

    @Override
    public String toString() {
        return "HoopBranchInvocation{" +
                "appName='" + appName + '\'' +
                ", beanId='" + beanId + '\'' +
                ", tryMethodName='" + tryMethodName + '\'' +
                ", commitMethodName='" + commitMethodName + '\'' +
                ", rollbackMethoName='" + rollbackMethoName + '\'' +
                ", param=" + Arrays.toString(param) +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", version='" + version + '\'' +
                ", order=" + order +
                '}';
    }
}
