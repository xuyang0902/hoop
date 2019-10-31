package com.tongbanjie.console.web.controller;

import com.tongbanjie.console.app.dao.HoopAppDao;
import com.tongbanjie.console.app.model.HoopApp;
import com.tongbanjie.console.app.model.HoopAppQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * hoopApp信息的增删改查
 *
 * @author xu.qiang
 * @date 18/9/4
 */
@RestController
public class HoopAppController {

    private static final Logger logger = LoggerFactory.getLogger(HoopAppController.class);

    @Autowired
    private HoopAppDao hoopAppDao;

    @RequestMapping(value = "/app/findById", method = {RequestMethod.POST, RequestMethod.GET})
    public HoopApp findById(HttpServletRequest request) {

        Long id = Long.valueOf(request.getParameter("id"));

        return hoopAppDao.findById(id);
    }

    @RequestMapping(value = "/app/list", method = {RequestMethod.POST, RequestMethod.GET})
    public List<HoopApp> get(HttpServletRequest request) {

        return hoopAppDao.query(new HoopAppQuery());
    }

    @RequestMapping(value = "/app/add", method = {RequestMethod.POST, RequestMethod.GET})
    public Boolean add(HttpServletRequest request) {

        try {
            String appName = request.getParameter("appName");
            String urls = request.getParameter("urls");

            HoopApp hoopApp = new HoopApp();
            hoopApp.setAppName(appName);
            hoopApp.setUrls(urls);
            return hoopAppDao.insert(hoopApp) == 1;
        } catch (Exception e) {
            logger.error(">>添加应用信息异常，e:", e);
            return false;
        }


    }

    @RequestMapping(value = "/app/update", method = {RequestMethod.POST, RequestMethod.GET})
    public Boolean update(HttpServletRequest request) {

        String appName = request.getParameter("appName");
        String urls = request.getParameter("urls");

        HoopApp hoopApp = new HoopApp();
        hoopApp.setAppName(appName);
        hoopApp.setUrls(urls);
        return hoopAppDao.updateByName(hoopApp) == 1;
    }


}
