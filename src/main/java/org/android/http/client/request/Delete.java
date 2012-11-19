package org.android.http.client.request;

import java.util.Map;

public class Delete extends HttpRequest {

    public Delete(String url, Map<String, String> params) {
        super(url, params);
        this.httpMethod = HttpMethod.DELETE;
    }

}
