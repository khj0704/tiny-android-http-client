package org.android.io.httpclient.response;

/**
 * @author Daniel
 */
public abstract class HttpCallback {

    abstract public void onSuccess(HttpResponse resp);

    abstract public void onError(HttpResponse resp);

    abstract public void onTimeout(HttpResponse resp);

    public void onSuccess1xx(HttpResponse resp) {
    }

    ;

    public void onSuccess2xx(HttpResponse resp) {
    }

    ;

    public void onSuccess3xx(HttpResponse resp) {
    }

    ;

    public void onError4xx(HttpResponse resp) {
    }

    ;

    public void onError5xx(HttpResponse resp) {
    }

    ;


}
