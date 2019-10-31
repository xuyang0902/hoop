package com.tongbanjie.console.web.controller;

import com.tongbanjie.console.app.model.LoginUser;
import com.tongbanjie.console.config.model.HoopProperties;
import com.tongbanjie.console.interceptor.SessionInterceptor;
import com.tongbanjie.console.util.SessionUtil;
import com.tongbanjie.hoop.api.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xu.qiang
 * @date 2018/9/5
 */
@Controller
public class LoginController {

    @Autowired
    private HoopProperties hoopProperties;


    @ResponseBody
    @RequestMapping(value = "/loginIn", method = RequestMethod.POST)
    public Boolean loginFor(HttpServletRequest request, HttpServletResponse response) {

        /**
         * 单台机器单机部署，登录先这样吧
         */
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (hoopProperties.getPassword().equals(password) && hoopProperties.getName().equals(username)) {
            //登录成功 写cookie
            String uuId = UUIDUtil.getUUId();
            LoginUser loginUser = new LoginUser();
            loginUser.setUserName(username);
            SessionInterceptor.cache.put(uuId, loginUser);
            SessionUtil.writeCookie(request, response, uuId);
            return true;
        }

        return false;
    }


    @ResponseBody
    @RequestMapping(value = "/loginOut", method = RequestMethod.POST)
    public Boolean loginOut(HttpServletRequest request) {

        String sessionId = SessionUtil.getSessionId(request);
        SessionInterceptor.cache.remove(sessionId);
        return true;
    }

}
