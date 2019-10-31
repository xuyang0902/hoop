package com.tongbanjie.console.util;

import org.apache.http.Header;

import java.util.Locale;

/**
 * @author kongyi
 * @since 2017-03-16
 */
public class HttpResponseWrapper {
    private int statusCode;
    private String content;
    private Header[] headers;
    private Locale locale;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
