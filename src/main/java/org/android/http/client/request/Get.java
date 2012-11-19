package org.android.http.client.request;

import java.util.Map;

/**
 * An HTTP GET request.
 * 
 * @author David M. Chandler
 */
public class Get extends HttpRequest {

    public Get(String url, Map<String, String> params) {
        super(url, params);
        this.httpMethod = HttpMethod.GET;
    }

}
