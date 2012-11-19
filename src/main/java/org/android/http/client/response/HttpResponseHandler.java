package org.android.http.client.response;

public interface HttpResponseHandler {

    public abstract void onComplete(HttpResponse httpResponse);
    
    public abstract void onError(Exception e);

}
