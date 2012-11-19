package org.android.http.client.response;

/**
 * The calling code can be notified when a request is complete or
 * has thrown an exception.
 */
public abstract class HttpResponseHandler {

    /**
     * Called when response is available or max retries exhausted. 
     * 
     * @param httpResponse may be null!
     */
    public abstract void onComplete(HttpResponse httpResponse);
    
    /**
     * Called when a non-recoverable exception has occurred.
     * Timeout exceptions are considered recoverable and won't
     * trigger this call.
     */
    public void onError(Exception e) {
        e.printStackTrace();
    }

}
