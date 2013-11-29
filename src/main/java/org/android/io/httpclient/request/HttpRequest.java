package org.android.io.httpclient.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpRequest {

    protected String url;
    protected HttpMethod httpMethod;
    protected String contentType;
    protected byte[] content;
    protected Map<String, String> headers = new HashMap<String, String>();

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

    public HttpRequest addHeader(String name, String value) {
        headers.put(name, value);
        return this;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static final class Get extends HttpRequest {
        public Get(String url, Map<String, String> params) {
            super(url, params);
            this.httpMethod = HttpMethod.GET;
        }
    }

    public static final class Post extends HttpRequest {
        public Post(String url, Map<String, String> params, String contentType, byte[] data) {
            super(url, params);
            this.httpMethod = HttpMethod.POST;
            this.contentType = contentType;
            this.content = data;
        }
    }

    public static final class Put extends HttpRequest {
        public Put(String url, Map<String, String> params, String contentType, byte[] data) {
            super(url, params);
            this.httpMethod = HttpMethod.PUT;
            this.contentType = contentType;
            this.content = data;
        }
    }

    public static final class Delete extends HttpRequest {
        public Delete(String url, Map<String, String> params) {
            super(url, params);
            this.httpMethod = HttpMethod.DELETE;
        }
    }

}
