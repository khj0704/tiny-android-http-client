package org.android.http.client;

import android.os.AsyncTask;
import org.android.http.client.cache.CacheManager;
import org.android.http.client.cookie.CookieManager;
import org.android.http.client.request.HttpMethod;
import org.android.http.client.request.HttpRequest;
import org.android.http.client.response.HttpResponse;
import org.android.http.client.response.HttpResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private static final int MAX_RETRY_TIMES = 3;

    private CookieManager cookieManager;
    private CacheManager cacheManager;
    private Map<String, String> defaultHeaders = new HashMap<String, String>();
    private int connectionTimeout = 10000;
    private int readTimeout = 10000;

    public HttpClient(Map<String, String> defaultHeaders, CookieManager cookieManager, CacheManager cacheManager) {
        this.defaultHeaders = defaultHeaders;
        this.cookieManager = cookieManager;
        this.cacheManager = cacheManager;
    }

    /**
     * In case you have your own async handler, you can chain your requests with this method. e.g,
     * resp = client.executeSync(req1);
     * if (resp ...)
     *     client.executeSync(req2);
     * else
     *     client.executeSync(req3);
     */
    public HttpResponse executeSync(HttpRequest httpRequest) throws HttpException {
        HttpResponse httpResponse = null;
        try {
            httpResponse = sendRequest(httpRequest.getUrl(),
                    httpRequest.getHttpMethod(), httpRequest.getContentType(),
                    httpRequest.getContent());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (HttpException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new HttpException(ex, httpResponse);
        }
        return httpResponse;
    }

    public void executeAsync(HttpRequest httpRequest, HttpResponseHandler handlerHttp) {
        new HttpRequestAsyncTask(this, handlerHttp).execute(httpRequest);
    }

    public HttpResponse sendRequest(HttpRequest httpRequest) {
        int retryCounter = 0;
        int maxRetries = httpRequest.getHttpMethod().isIdemponent() ? MAX_RETRY_TIMES : 0;
        do {
            try {
                return sendRequest(httpRequest.getUrl(), httpRequest.getHttpMethod(),
                        httpRequest.getContentType(), httpRequest.getContent());
            } catch (Exception e) {
                if (retryCounter >= maxRetries) {
                    e.printStackTrace();
                } else {
                    try { Thread.sleep(500); } catch (InterruptedException ignored) { }
                }
            }
        } while (retryCounter < MAX_RETRY_TIMES);
        return null;
    }


    protected HttpResponse sendRequest(String url, HttpMethod httpMethod, String contentType,
                                       byte[] content) throws HttpException {

        HttpURLConnection conn = null;
        HttpResponse resp = null;

        try {
            conn = openConnection(url, httpMethod, contentType);
            if (conn.getDoOutput() && content != null) {
                writeStream(conn, content);
            }
            if (conn.getDoInput()) {
                resp = new HttpResponse(conn, readStream(conn.getInputStream()));
            } else {
                resp = new HttpResponse(conn, null);
            }
        } catch (Exception e) {
            try {
                resp = new HttpResponse(conn, readStream(conn.getErrorStream()));
                if (resp != null && resp.getStatus() > 0) {
                    return resp;
                } else {
                    throw new HttpException(e, resp);
                }
            } catch (HttpException ex) {
                throw ex;
            } catch (IOException ex) {
                throw new HttpException(ex, null);
            } catch (RuntimeException ex) {
                throw ex;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return resp;
    }

    protected HttpURLConnection openConnection(String url,  HttpMethod method, String contentType) throws IOException {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url + " is not a valid URL", e);
        }
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestMethod(method.getMethodName());
        conn.setDoOutput(method.isDoOutput());
        conn.setDoInput(method.isDoInput());
        if (this.defaultHeaders != null) {
            for (String name : defaultHeaders.keySet()) {
                String value = defaultHeaders.get(name);
                conn.setRequestProperty(name, value);
            }
        }
        if (contentType != null) {
            conn.setRequestProperty("Content-Type", contentType);
        }
        conn.setRequestProperty("Accept-Charset", "utf8");
        return conn;
    }

    protected int writeStream(HttpURLConnection conn, byte[] content) throws Exception {
        OutputStream out = null;
        try {
            out = conn.getOutputStream();
            out.write(content);
            return conn.getResponseCode();
        } finally {
            if (out != null) {
                try { out.close(); } catch (Exception ignored) { }
            }
        }
    }

    public byte[] readStream(InputStream in) throws IOException {
        if (in == null) return null;
        try {
            byte[] data = new byte[16384];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            while ((len = in.read(data)) != -1) {
                baos.write(data, 0, len);
            }
            baos.flush();
            return baos.toByteArray();
        } finally {
            try { in.close(); } catch (Exception ignored) { }
        }
    }

    public class HttpRequestAsyncTask extends AsyncTask<HttpRequest, Void, HttpResponse> {

        private HttpClient client;
        private HttpResponseHandler respHandler;
        private Exception respException;

        public HttpRequestAsyncTask(HttpClient httpClient, HttpResponseHandler respHandler) {
            this.client = httpClient;
            this.respHandler = respHandler;
        }

        @Override
        protected HttpResponse doInBackground(HttpRequest... params) {
            try {
                if (params != null && params.length > 0) {
                    HttpRequest httpRequest = params[0];
                    return client.sendRequest(httpRequest);
                } else {
                    throw new IllegalArgumentException("Invalid params: " + Arrays.toString(params));
                }
            } catch (Exception e) {
                respException = e;
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            respHandler.onComplete(result);
        }

        @Override
        protected void onCancelled() {
            respHandler.onError(respException);
        }

    }

}