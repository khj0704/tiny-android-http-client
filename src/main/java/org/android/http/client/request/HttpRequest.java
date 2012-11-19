package org.android.http.client.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Holds data for an HTTP request to be made with the attached HTTP client.
 * 
 * @author David M. Chandler
 */
public abstract class HttpRequest {
    
    protected String path;
    protected HttpMethod httpMethod;
    protected String contentType;
    protected byte[] content;
    
    /**
     * Constructs a request with optional params appended
     * to the query string.
     */
    public HttpRequest(String url, Map<String, String> params) {
        if (url == null) {
            throw new RuntimeException("Request URL cannot be null");
        }
        this.path = url;
        if (params != null) {
            this.path += "?" + urlEncode(params);
        }
    }

    public String getPath() {
        return path;
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

    protected String urlEncode(Map<String, String> map) {
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

    protected byte[] urlEncodedBytes(Map<String, String> map) {
        byte[] bytes = null;
        try {
            bytes = urlEncode(map).getBytes("utf8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("impossible to reach", e);
        }
        return bytes;
    }

}
