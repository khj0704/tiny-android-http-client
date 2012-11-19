package org.android.http.client.request;

import java.util.Map;

public class Post extends HttpRequest {

    public Post(String url, Map<String, String> params, String contentType, byte[] data) {
        super(url, params);
        this.httpMethod = HttpMethod.POST;
        this.contentType = contentType;
        this.content = data;
    }

}
