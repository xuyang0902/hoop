package com.tongbanjie.console.interceptor;

import com.tongbanjie.console.app.model.LoginUser;
import com.tongbanjie.console.util.SessionUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

public class SessionInterceptor implements HandlerInterceptor {


    public static final ConcurrentHashMap<String, LoginUser> cache = new ConcurrentHashMap<String, LoginUser>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {


        String sessionId = SessionUtil.getSessionId(request);

        if (StringUtils.isEmpty(sessionId)) {
            response.sendRedirect("/login");
            return false;
        }


        LoginUser loginUser = cache.get(sessionId);
        if (loginUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        SessionUtil.setCurrMember(loginUser);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        SessionUtil.removeCurrMember();
    }
}