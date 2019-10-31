package com.tongbanjie.console.web.model;

import com.tongbanjie.console.app.model.HoopApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author xu.qiang
 * @date 18/9/4
 */
public class HoopAppVo implements Serializable {

    private static final long serialVersionUID = 5180272014521591547L;
    //
    private Long id;
    // 接入系统名称
    private String appName;
    // urls
    private List<String> urls;
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

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }


    public static List<HoopAppVo> convert(List<HoopApp> list) {
        List<HoopAppVo> uiList = new ArrayList<HoopAppVo>(list.size());
        for (HoopApp hoopApp : list) {
            HoopAppVo ui = new HoopAppVo();
            ui.setId(hoopApp.getId());
            ui.setAppName(hoopApp.getAppName());

            String[] split = hoopApp.getUrls().split(",");
            List<String> urls = new ArrayList<String>();
            for (String s : split) {
                urls.add(s);
            }
            ui.setUrls(urls);
            ui.setCreateTime(hoopApp.getCreateTime());
            ui.setModifyTime(hoopApp.getModifyTime());
            uiList.add(ui);
        }

        return uiList;


    }
}