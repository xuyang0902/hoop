package com.tongbanjie.console.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.tongbanjie.console.app.dao.HoopAppDao;
import com.tongbanjie.console.app.model.HoopApp;
import com.tongbanjie.console.app.model.HoopAppQuery;
import com.tongbanjie.console.invoke.HttpInvoker;
import com.tongbanjie.console.util.HttpClientUtils;
import com.tongbanjie.console.web.model.HoopAppHealthVo;
import com.tongbanjie.console.web.model.HoopAppVo;
import com.tongbanjie.hoop.core.servlet.constant.ReqType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 常用页面
 *
 * @author xu.qiang
 * @date 18/9/6
 */
@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @Autowired
    private HoopAppDao hoopAppDao;

    /**
     * 应用列表
     */
    @RequestMapping(value = "/index")
    public String index(Model model) {

        model.addAttribute("list", HoopAppVo.convert(hoopAppDao.query(new HoopAppQuery())));

        return "/index";
    }

    /**
     * 添加应用
     */
    @RequestMapping(value = "/addApp")
    public String addApp(Model model) {

        return "/pages/addapp";
    }

    /**
     * 添加应用
     */
    @RequestMapping(value = "/updateApp/{id}")
    public String updateApp(@PathVariable Long id, Model model) {

        HoopApp hoopApp = hoopAppDao.findById(id);

        model.addAttribute("hoopApp", hoopApp);
        return "/pages/updateapp";
    }

    /**
     * 健康检查
     */
    @RequestMapping(value = "/healthy")
    public String healthy(Model model) {

        List<HoopApp> list = hoopAppDao.query(new HoopAppQuery());

        List<HoopAppHealthVo> healthUIList = new ArrayList<HoopAppHealthVo>();
        for (HoopApp hoopApp : list) {
            String[] split = hoopApp.getUrls().split(",");

            for (String url : split) {
                HoopAppHealthVo healthUI = new HoopAppHealthVo();
                healthUI.setAppName(hoopApp.getAppName());
                healthUI.setUrl(url);

                String healthyStatus = "UNKNOWN";
                try {
                    JSONObject json = new JSONObject();
                    json.put("reqType", ReqType.HEALTHY_CHECK);

                    healthyStatus = HttpClientUtils.post(url, json.toString(),
                            "application/json", "utf-8",
                            1000, 1000);


                } catch (Exception e) {
                    logger.warn(">> Healthy check failed ur:{}", url);
                }

                healthUI.setStatus(healthyStatus);
                healthUIList.add(healthUI);
            }
        }
        model.addAttribute("list", healthUIList);

        return "/pages/healthy";
    }

    /**
     * 登录页面
     */
    @RequestMapping(value = "/login")
    public String loginIndex() {
        return "/login";
    }

    /**
     * 版本说明
     *
     * @return
     */
    @RequestMapping(value = "/version")
    public String version() {
        return "/version";
    }

}
