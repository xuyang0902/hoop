package com.tongbanjie.console.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author xu.qiang
 * @date 18/9/4
 */
public class HoopAppHealthVo implements Serializable {

    private static final long serialVersionUID = 5180272014521591547L;

    // 接入系统名称
    private String appName;

    // urls
    private String url;

    //节点状态
    private String status;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}