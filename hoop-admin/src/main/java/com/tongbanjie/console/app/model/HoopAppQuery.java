package com.tongbanjie.console.app.model;

import java.io.Serializable;

/**
 * hoopapp查询功能
 * @author xu.qiang
 * @date 18/9/4
 */
public class HoopAppQuery implements Serializable {

    private Long id;

    /**
     * 接入系统名称
     */
    private String appName;

    private Integer offset = 0;
    private Integer limit = 100;


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

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
