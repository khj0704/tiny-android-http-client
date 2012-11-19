package org.android.http.client;

import org.android.http.client.response.HttpResponse;

public class HttpException extends Exception {

    private HttpResponse response;
    
    public HttpException(Exception e, HttpResponse resp) {
        super(e);
        this.response = resp;
    }

    public HttpResponse getResponse() {
        return response;
    }

}
