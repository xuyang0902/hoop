package com.tongbanjie.console.app.model;

import java.io.Serializable;
import java.util.Date;


/**
 * @author xu.qiang
 * @date 18/9/4
 */
public class HoopApp implements Serializable {

    private static final long serialVersionUID = 5180272014521591547L;
    //
    private Long id;
    // 接入系统名称
    private String appName;
    // urls
    private String urls;
    // 创建时间
    private Date createTime;
    // 修改时间
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}