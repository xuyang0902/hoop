package com.tongbanjie.console.config.model;

import java.io.Serializable;

/**
 * @author xu.qiang
 * @date 2018/9/5
 */
public class HoopProperties implements Serializable {


    private String name;

    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
