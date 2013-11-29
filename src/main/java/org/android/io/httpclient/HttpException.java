package org.android.io.httpclient;

import org.android.io.httpclient.response.HttpResponse;

/**
 * @author Daniel
 */
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
