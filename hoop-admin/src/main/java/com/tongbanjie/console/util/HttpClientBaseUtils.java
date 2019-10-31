package com.tongbanjie.console.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建这个类是因为...有时候需要得到{@link org.apache.http.StatusLine#getStatusCode()}
 * 如果只需要获取ResponseBody中的内容，直接使用{@link HttpClientUtils}
 *
 * @author kongyi
 * @since 2017-02-07
 */
public abstract class HttpClientBaseUtils {

    protected static final String DEFAULT_MIME = "text/plain";
    protected static final String PREFIX_HTTPS = "https";

    private static CloseableHttpClient client = null;
    private static PoolingHttpClientConnectionManager cm;

    static {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }

    public static PoolStats getPoolsats(HttpRoute httpRoute){
        return cm.getStats(httpRoute);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param uri The string to be parsed into a URI
     * @param headers Request Headers
     * @param body RequestBody
     * @param mimeType 例如 application/xml, 默认: text/plain
     * @param charset 编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return An {@link HttpResponse}
     *
     * @throws ConnectTimeoutException 建立链接超时异常
     * @throws SocketTimeoutException 响应超时
     */
    public static HttpResponseWrapper post(String uri, Map<String, String> headers, String body, String mimeType, String charset, Integer connTimeout, Integer readTimeout) throws IOException, GeneralSecurityException {
        CloseableHttpClient client = null;
        HttpPost post = new HttpPost(uri);

        if (StringUtils.isBlank(mimeType)) {
            mimeType = DEFAULT_MIME;
        }

        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse resp;
            if (uri.startsWith(PREFIX_HTTPS)) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                resp = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientBaseUtils.client;
                resp = client.execute(post);
            }
            return convet2Wrapper(resp, charset, null);
        } finally {
            post.releaseConnection();
            if (client != null && uri.startsWith(PREFIX_HTTPS)) {
                client.close();
            }
        }
    }

    /**
     * 提交form表单，可以设置charset，如果charset为null，默认UTF-8
     *
     * @param uri Request-URI
     * @param params 表单参数
     * @param headers Request Headers
     * @param charset 字符集编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return An {@link HttpResponse}
     *
     * @throws ConnectTimeoutException
     * @throws SocketTimeoutException
     */
    public static HttpResponseWrapper postForm(String uri, Map<String, String> params, Map<String, String> headers, String charset, Integer connTimeout, Integer readTimeout) throws GeneralSecurityException, IOException {
        CloseableHttpClient client = null;
        HttpPost post = new HttpPost(uri);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity;
                if (StringUtils.isNotBlank(charset)) {
                    entity = new UrlEncodedFormEntity(formParams, Charset.forName(charset.toUpperCase()));
                } else {
                    entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                }
                post.setEntity(entity);
            }
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());
            HttpResponse resp;
            if (uri.startsWith(PREFIX_HTTPS)) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                resp = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientBaseUtils.client;
                resp = client.execute(post);
            }
            return convet2Wrapper(resp, charset, Consts.UTF_8.name());
        } finally {
            post.releaseConnection();
            if (client != null && uri.startsWith(PREFIX_HTTPS)) {
                client.close();
            }
        }
    }

    /**
     * 发送一个 GET 请求
     *
     * @param uri The string to be parsed into a URI
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return An {@link HttpResponse}
     *
     * @throws ConnectTimeoutException 建立链接超时
     * @throws SocketTimeoutException 响应超时
     */
    public static HttpResponseWrapper get(String uri, String charset, Integer connTimeout, Integer readTimeout) throws IOException, GeneralSecurityException {
        CloseableHttpClient client = null;
        HttpGet get = new HttpGet(uri);
        try {
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            get.setConfig(customReqConf.build());
            HttpResponse resp;
            if (uri.startsWith(PREFIX_HTTPS)) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                resp = client.execute(get);
            } else {
                // 执行 Http 请求.
                client = HttpClientBaseUtils.client;
                resp = client.execute(get);
            }
            return convet2Wrapper(resp, charset, null);
        } finally {
            get.releaseConnection();
            if (client != null && uri.startsWith(PREFIX_HTTPS)) {
                client.close();
            }
        }
    }

    public static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

        });
        return HttpClients.custom().setSSLSocketFactory(sslsf).setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }

    /**
     * 从 response 里获取 charset
     *
     * @param response
     * @return
     */
    @SuppressWarnings("unused")
    private static String getCharsetFromResponse(HttpResponse response) {
        // Content-Type:text/html; charset=GBK
        if (response.getEntity() != null && response.getEntity().getContentType() != null && response.getEntity().getContentType().getValue() != null) {
            String contentType = response.getEntity().getContentType().getValue();
            if (contentType.contains("charset=")) {
                return contentType.substring(contentType.indexOf("charset=") + 8);
            }
        }
        return null;
    }

    private static HttpResponseWrapper convet2Wrapper(HttpResponse resp, String charset, String defaultCharset) throws IOException {
        HttpResponseWrapper wrapper = new HttpResponseWrapper();
        wrapper.setStatusCode(resp.getStatusLine().getStatusCode());
        wrapper.setHeaders(resp.getAllHeaders());
        wrapper.setLocale(resp.getLocale());

        HttpEntity entity = resp.getEntity();
        if (null == entity) {
            return wrapper;
        }

        if (StringUtils.isBlank(charset) && StringUtils.isNotBlank(defaultCharset)) {
            charset = defaultCharset;
        }

        wrapper.setContent(EntityUtils.toString(resp.getEntity(), charset));
        return wrapper;
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i <= 50; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpResponseWrapper response = post("http://ip.taobao.com/service/getIpInfo.php?ip=122.224.137.178", null, null, null, Consts.UTF_8.name(), 3000, 10000);
                        System.out.println(Thread.currentThread().getName() + "\r\n" + JSON.toJSONString(response));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
    }
}
