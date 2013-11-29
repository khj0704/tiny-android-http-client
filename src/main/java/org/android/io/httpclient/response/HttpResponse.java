package org.android.io.httpclient.response;

import org.android.io.httpclient.util.LOG;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel
 */
public class HttpResponse {

    private static final LOG logger = LOG.get(HttpResponse.class);

    private int status;
    private String url;
    private Map<String, List<String>> headers;
    protected byte[] body;
    private Exception exception;

    public HttpResponse(Exception ex) {
        this.exception = ex;
        logger.i(ex, "Response error from url:", url, ", exception=", ex.getClass(), ":", ex.getMessage());
    }

    public HttpResponse(int status, String url, Map<String, List<String>> headers, byte[] body) {
        this.status = status;
        this.url = url;
        this.headers = headers;
        this.body = body;
        logger.i("Response " + status + " from url:", url, ", body=", body == null ? "null" : new String(body));
    }

    public Exception getException() {
        return exception;
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

    public String getBodyString() {
        try {
            if (body != null) {
                return new String(body, "utf8");
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return "";
    }
}
