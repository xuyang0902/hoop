/**
 *
 */
package com.tongbanjie.console.util;

import com.tongbanjie.console.app.model.LoginUser;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * session管理
 */
public class SessionUtil {

    private static final ThreadLocal<LoginUser> MEMBER_SESSION = new ThreadLocal<LoginUser>();

    private static final String SESSIONID_COOKIE_NAME = "HOOP_SESSION_ID";

    public static final String COOKIE_DOMAIN = ".tongbanjie.com";
    public static final String COOKIE_PATH = "/";
    public static final int COOKIE_MAX_AGE = -1;//浏览器进程关闭 cookie失效

    /**
     * 从本地线程变量中删除当前存放的值，因为服务器的线程池线程不一定会释放
     */
    public static void removeCurrMember() {
        MEMBER_SESSION.remove();
    }

    /**
     * 设置当前的登录人信息到本地线程变量中
     *
     * @param member
     */
    public static void setCurrMember(LoginUser member) {
        MEMBER_SESSION.set(member);
    }

    /**
     * 获取当前登录的member
     *
     * @return
     */
    public static LoginUser getCurrMember() {
        return MEMBER_SESSION.get();
    }

    /**
     * 获取当前登录用户的ID
     *
     * @return
     */
    public static String getCurrUserName() {
        if (getCurrMember() == null) {
            return null;
        }
        return getCurrMember().getUserName();
    }

    /**
     * 获取sessionId
     *
     * @param request
     * @return
     */
    public static String getSessionId(HttpServletRequest request) {
        String jsessionId = null;

        jsessionId = request.getParameter(SESSIONID_COOKIE_NAME);

        if (!StringUtils.isBlank(jsessionId)) {
            jsessionId = jsessionId.replace(";", "");
            return jsessionId;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            // 从 cookies 中获取.
            for (Cookie cookie : cookies) {
                if (SESSIONID_COOKIE_NAME.equals(cookie.getName())) {
                    jsessionId = cookie.getValue();
                }
            }
        }
        return jsessionId;
    }

    public static void writeCookie(HttpServletRequest request, HttpServletResponse response, String ssoSessionId) {
        // 写 Cookie
        Cookie c = new Cookie(SESSIONID_COOKIE_NAME, ssoSessionId);
        c.setMaxAge(COOKIE_MAX_AGE);
        String requestUrl = (request).getRequestURL().toString();
        if (StringUtils.contains(requestUrl, "tongbanjie.com")) {
            c.setDomain(COOKIE_DOMAIN);
        }
        c.setPath(COOKIE_PATH);
        response.addCookie(c);

    }


    public static boolean isLogin() {
        return getCurrMember() != null;
    }

}
