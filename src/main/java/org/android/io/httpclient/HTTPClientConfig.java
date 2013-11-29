package org.android.io.httpclient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel
 */
public class HTTPClientConfig {

    private int maxRetryTimes = 3;
    private Map<String, String> defaultHeaders = new HashMap<String, String>();
    private int connectionTimeout = 10000;
    private int readTimeout = 10000;
    private boolean responseHeadersInLowerCase = false;

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isResponseHeadersInLowerCase() {
        return responseHeadersInLowerCase;
    }

    public void setResponseHeadersInLowerCase(boolean responseHeadersInLowerCase) {
        this.responseHeadersInLowerCase = responseHeadersInLowerCase;
    }

}
