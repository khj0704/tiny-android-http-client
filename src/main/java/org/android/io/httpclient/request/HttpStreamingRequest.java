package org.android.io.httpclient.request;

import java.io.InputStream;
import java.util.Map;

/**
 * @author Daniel
 *         If you want to upload a file, you must use this request to stream the request
 */
public abstract class HttpStreamingRequest extends HttpRequest {

    private final InputStream inStream;

    public HttpStreamingRequest(String url, Map<String, String> params, InputStream inStream) {
        super(url, params);
        this.inStream = inStream;
    }

    public InputStream getInputStream() {
        return inStream;
    }

    public static final class Get extends HttpStreamingRequest {
        public Get(String url, Map<String, String> params) {
            super(url, params, null);
            this.httpMethod = HttpMethod.GET;
        }
    }

    public static final class Post extends HttpStreamingRequest {
        public Post(String url, Map<String, String> params, String contentType, InputStream in) {
            super(url, params, in);
            this.httpMethod = HttpMethod.POST;
            this.contentType = contentType;
        }
    }

    public static final class Put extends HttpStreamingRequest {
        public Put(String url, Map<String, String> params, String contentType, InputStream in) {
            super(url, params, in);
            this.httpMethod = HttpMethod.PUT;
            this.contentType = contentType;
        }
    }

}
