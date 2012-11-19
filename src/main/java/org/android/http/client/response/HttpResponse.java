package org.android.http.client.response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    
    private int status;
    private String url;
    private Map<String, List<String>> headers;
    private byte[] body;
    
    public HttpResponse(HttpURLConnection conn, byte[] body) throws IOException {
        this.status = conn.getResponseCode();
        this.url = conn.getURL().toString();
        this.headers = conn.getHeaderFields();
        this.body = body;
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getUrl() {
        return url;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
    
    public byte[] getBody() {
        return body;
    }

}
