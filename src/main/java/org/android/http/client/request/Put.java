package org.android.http.client.request;

import java.util.Map;

public class Put extends HttpRequest {

    public Put(String url, Map<String, String> params, String contentType, byte[] data) {
        super(url, params);
        this.httpMethod = HttpMethod.PUT;
        this.contentType = contentType;
        this.content = data;
    }

}
