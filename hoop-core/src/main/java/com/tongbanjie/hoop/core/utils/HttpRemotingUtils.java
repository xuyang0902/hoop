package com.tongbanjie.hoop.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * htttp远程调用工工具
 *
 * @author xu.qiang
 * @date 2017/5/15.
 */
public class HttpRemotingUtils {

    private static final String CHAR_SET = "UTF-8";


    public static JSONObject getRequestFromHttpRequest(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String input;
        while ((input = reader.readLine()) != null) {
            sb.append(input);
        }

        return JSONObject.parseObject(sb.toString());
    }

    /**
     * 从httpRequest获取请求对象
     */
    public static <T> T getRequestFromHttpRequest(HttpServletRequest request, Class<T> t)
            throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String input;
        while ((input = reader.readLine()) != null) {
            sb.append(input);
        }
        String requestConent = sb.toString();
        try {
            return JSON.parseObject(requestConent, t);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将结果返回给server端
     */
    public static void writeResponse(HttpServletResponse response, Object obj)
            throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        String respBody = JSON.toJSONString(obj);
        try {
            outputStream.write(respBody.getBytes(CHAR_SET));
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }


}
