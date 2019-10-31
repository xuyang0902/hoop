package com.tongbanjie.console.util;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpClientUtils, 使用 HttpClient 4.x<br>
 * 升级为 4.x 是因为 3.x 多线程环境下设置超时时间会对全局造成影响.
 *
 * @author sunyi
 *
 * @see HttpClientBaseUtils
 */
public abstract class HttpClientUtils {

    /**
     * @deprecated 为了兼容旧代码所以存在此方法.<br>
     *             1. contentType 此方法中使用了完整 contentType,比如[application/xml; charset=GBK], 应该使用更标准的 mimeType 以及 charset<br>
     *             2. HttpClient 超时设定使用的是 Int32 , 所以这里也不推荐再使用 Int64. <br>
     */
    public static String post(String url, String body, String contentType, String encode, long readTimeout) throws IOException, GeneralSecurityException {
        String mimeType = contentType;
        if (StringUtils.isNotBlank(contentType) && contentType.contains(";")) {
            mimeType = contentType.substring(0, contentType.indexOf(";")).trim();
        }
        return post(url, body, mimeType, encode, null, Long.valueOf(readTimeout).intValue());
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url Request-URI
     * @param body RequestBody
     * @param mimeType 例如 application/xml, 默认: text/plain
     * @param charset 编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws ConnectTimeoutException 建立链接超时异常
     * @throws SocketTimeoutException 响应超时
     */
    public static String post(String url, String body, String mimeType, String charset, Integer connTimeout, Integer readTimeout) throws IOException, GeneralSecurityException {
        return post(url, null, body, mimeType, charset, connTimeout, readTimeout);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url Request-URI
     * @param headers Request Headers
     * @param body RequestBody
     * @param mimeType 例如 application/xml, 默认: text/plain
     * @param charset 编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws ConnectTimeoutException 建立链接超时异常
     * @throws SocketTimeoutException 响应超时
     */
    public static String post(String url, Map<String, String> headers, String body, String mimeType, String charset, Integer connTimeout, Integer readTimeout) throws IOException, GeneralSecurityException {
        HttpResponseWrapper respWrapper = HttpClientBaseUtils.post(url, headers, body, mimeType, charset, connTimeout, readTimeout);
        return respWrapper.getContent();
    }

    /**
     * 提交form表单
     *
     * @param url Request-URI
     * @param params 表单参数
     * @param headers Request Headers
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     * @throws ConnectTimeoutException
     * @throws SocketTimeoutException
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout, Integer readTimeout) throws GeneralSecurityException, IOException {
        return postForm(url, params, headers, null, connTimeout, readTimeout);
    }

    /**
     * 提交form表单，可以设置charset，如果charset为null，默认UTF-8
     *
     * @param url Request-URI
     * @param params 表单参数
     * @param headers Request Headers
     * @param charset 字符集编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws ConnectTimeoutException
     * @throws SocketTimeoutException
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers, String charset, Integer connTimeout, Integer readTimeout) throws GeneralSecurityException, IOException {
        HttpResponseWrapper respWrapper = HttpClientBaseUtils.postForm(url, params, headers, charset, connTimeout, readTimeout);
        return respWrapper.getContent();
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url Request-URI
     * @param charset 指定的字符集编码
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws Exception
     */
    public static String get(String url, String charset) throws Exception {
        return get(url, charset, null, null);
    }

    /**
     * 发送一个 GET 请求
     *
     * @param url Request-URI
     * @param charset 指定的字符集编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     *
     * @throws ConnectTimeoutException 建立链接超时
     * @throws SocketTimeoutException 响应超时
     */
    public static String get(String url, String charset, Integer connTimeout, Integer readTimeout) throws IOException, GeneralSecurityException {
        HttpResponseWrapper respWrapper = HttpClientBaseUtils.get(url, charset, connTimeout, readTimeout);
        return respWrapper.getContent();
    }

    public static void main(String[] args) {
        try {
            Map<String, String> header = new HashMap<String, String>();
            header.put("CLIENT_VERSION", "3.1.0");
            header.put("Cookie", "JSESSIONID=538a385d4bf945e4a05ef44b0721fcd1;");
            header.put("MOBILE_DEVICE", "ANDROID_PHONE");

            // trade.tongbanjie.com
            // 192.168.1.109:8101
            String string = post("http://192.168.1.121:8173/status.jsp", header, null, null, "utf-8",
                    10000, 10000);
            System.out.println(string);
            System.out.println(string.length()); // 2178129

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
