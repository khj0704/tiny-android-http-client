package org.android.http.client.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class HttpRequest {
    
    protected String url;
    protected HttpMethod httpMethod;
    protected String contentType;
    protected byte[] content;
    
    public HttpRequest(String url, Map<String, String> params) {
        if (url == null) {
            throw new RuntimeException("Request URL cannot be null");
        }
        this.url = url;
        if (params != null) {
            this.url += "?" + urlEncode(params);
        }
    }

    public String getUrl() {
        return url;
    }
    
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public byte[] getContent() {
        return content;
    }

    private String urlEncode(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key);
            String value = map.get(key);
            if (value != null) {
                sb.append("=");
                try {
                    sb.append(URLEncoder.encode(value, "utf8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("impossible to reach", e);
                }
            }
        }
        return sb.toString();
    }

}
