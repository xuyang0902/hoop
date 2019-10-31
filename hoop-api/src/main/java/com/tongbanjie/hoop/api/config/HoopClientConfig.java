package com.tongbanjie.hoop.api.config;

/**
 * hoop客户端的配置配置
 *
 * @author xu.qiang
 * @date 18/8/20
 */
public class HoopClientConfig {

    private String appName = "hoop_app";

    //默认每隔3分钟 recover线程重跑一次
    private int recoverTimeInterval = 3;

    //默认查询3分钟前的事务单据进行补偿
    private int beforTime = 3;

    //最大恢复次数 默认1000次
    private int maxRecoverCount = 1000;

    //清理事务日志 默认清理
    private boolean removeLog = true;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getRecoverTimeInterval() {
        return recoverTimeInterval;
    }

    public void setRecoverTimeInterval(int recoverTimeInterval) {
        this.recoverTimeInterval = recoverTimeInterval;
    }

    public int getBeforTime() {
        return beforTime;
    }

    public void setBeforTime(int beforTime) {
        this.beforTime = beforTime;
    }

    public int getMaxRecoverCount() {
        return maxRecoverCount;
    }

    public void setMaxRecoverCount(int maxRecoverCount) {
        this.maxRecoverCount = maxRecoverCount;
    }

    public boolean isRemoveLog() {
        return removeLog;
    }

    public void setRemoveLog(boolean removeLog) {
        this.removeLog = removeLog;
    }
}
