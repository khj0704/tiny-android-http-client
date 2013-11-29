package org.android.io.httpclient;

import org.android.io.httpclient.request.HttpMethod;
import org.android.io.httpclient.request.HttpRequest;
import org.android.io.httpclient.request.HttpStreamingRequest;
import org.android.io.httpclient.response.HttpCallback;
import org.android.io.httpclient.response.HttpResponse;
import org.android.io.httpclient.response.HttpStreamingResponse;
import org.android.io.httpclient.util.LOG;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Daniel
 *         Known issues
 *         The response header could be converted into lower case on some phones, I suggest you always ignore case
 *         when comparing or finding headers, or simply always convert reponse to lower case with
 *         <code>HTTPClientConfig.setResponseHeadersInLowerCase</code> to unify the headers.
 */
public class HTTPClient {

    private static final LOG logger = LOG.get(HTTPClient.class);
    private HTTPClientConfig config;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public HTTPClient(HTTPClientConfig config) {
        this.config = config;
    }

    public HTTPClientConfig getConfig() {
        return config;
    }

    /**
     * In case you have your own async handler, you can chain your requests with this method. e.g,
     * resp = client.executeSync(req1);
     * if (resp ...)
     * client.executeSync(req2);
     * else
     * client.executeSync(req3);
     */
    public HttpResponse executeSync(HttpRequest httpRequest) throws HttpException {
        HttpResponse httpResponse = null;
        try {
            httpResponse = sendRequestOnce(httpRequest);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (HttpException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new HttpException(ex, httpResponse);
        }
        return httpResponse;
    }

    public HttpStreamingResponse executeSync(HttpStreamingRequest httpRequest) throws HttpException {
        HttpStreamingResponse httpResponse = null;
        try {
            httpResponse = sendRequestOnce(httpRequest);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (HttpException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new HttpException(ex, httpResponse);
        }
        return httpResponse;
    }

    public FutureResponse executeAsync(HttpRequest httpRequest) {
        HttpRequestWorker task = new HttpRequestWorker(this, httpRequest);
        Future<HttpResponse> result = executor.submit(task);
        return new FutureResponse(result);
    }

    public void executeCallback(HttpRequest httpRequest, HttpCallback callback) {
        HttpRequestWorker task = new HttpRequestWorker(this, httpRequest, callback);
        executor.submit(task);
    }

    protected HttpResponse sendRequestWithRetry(HttpRequest httpRequest) throws HttpException {
        int retryCounter = 0;
        int maxRetries = httpRequest.getHttpMethod().isIdemponent() ? config.getMaxRetryTimes() : 0;
        if (httpRequest instanceof HttpStreamingRequest) {
            maxRetries = 1; // for streaming, don't retry. the stream cannot be reused.
        }
        do {
            try {
                if (httpRequest instanceof HttpStreamingRequest) {
                    HttpStreamingRequest streamingRequest = (HttpStreamingRequest) httpRequest;
                    return sendRequestOnce(streamingRequest);
                } else {
                    return sendRequestOnce(httpRequest);
                }
            } catch (Exception e) {
                if (++retryCounter >= maxRetries) {
                    e.printStackTrace();
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        } while (retryCounter < config.getMaxRetryTimes());
        return new HttpResponse(503, httpRequest.getUrl(), null, null);
    }

    private HttpStreamingResponse sendRequestOnce(HttpStreamingRequest request) throws HttpException {
        logger.d("Http request:", request.getUrl());
        InputStream in = request.getInputStream();
        try {
            HttpURLConnection conn =
                    openConnection(request.getUrl(), request.getHttpMethod(), request.getContentType());
            if (conn.getDoOutput() && in != null) {
                OutputStream out = conn.getOutputStream();
                byte[] buf = new byte[1024 * 64];
                int len;
                while ((len = in.read(buf)) >= 0) {
                    out.write(buf, 0, len);
                }
            }
            return buildHttpStreamingResponse(conn);
        } catch (Exception ex) {
            throw new HttpException(ex, null);
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException ignored) {
            }
        }

    }

    protected HttpResponse sendRequestOnce(HttpRequest request) throws HttpException {
        logger.d("Http request:", request.getUrl());
        HttpURLConnection conn = null;
        try {
            conn = openConnection(request.getUrl(), request.getHttpMethod(), request.getContentType());
            if (conn.getDoOutput() && request.getContent() != null) {
                writeStream(conn, request.getContent());
            }
            if (conn.getDoInput()) {
                return buildHttpResponse(conn, readStream(conn.getInputStream()));
            } else {
                return buildHttpResponse(conn, null);
            }
        } catch (Exception e) {
            logger.e(e);
            try {
                HttpResponse resp = buildHttpResponse(conn, readStream(conn.getErrorStream()));
                if (resp != null && resp.getStatus() > 0) {
                    return resp;
                } else {
                    throw new HttpException(e, resp);
                }
            } catch (IOException ex) {
                throw new HttpException(ex, null);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private HttpResponse buildHttpResponse(HttpURLConnection conn, byte[] bytes) {
        try {
            if (conn.getHeaderFields() != null) {
                Map<String, List<String>> headers = new HashMap<String, List<String>>(conn.getHeaderFields());
                logger.d("Http response headers:", headers);
                if (config.isResponseHeadersInLowerCase()) {
                    Map<String, List<String>> headersLowerCase = new HashMap<String, List<String>>();
                    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                        String key = entry.getKey();
                        if (key == null) continue; // ignore the status line whose key is null
                        List<String> values = entry.getValue();
                        headersLowerCase.put(key.toLowerCase(), values);
                        headers = headersLowerCase;
                    }
                }
                return new HttpResponse(conn.getResponseCode(), conn.getURL().toString(), headers, bytes);
            } else {
                return new HttpResponse(conn.getResponseCode(), conn.getURL().toString(), null, bytes);
            }
        } catch (IOException e) {
            return new HttpResponse(e);
        }
    }

    private HttpStreamingResponse buildHttpStreamingResponse(HttpURLConnection conn) {
        try {
            if (conn.getHeaderFields() != null) {
                Map<String, List<String>> headers = new HashMap<String, List<String>>(conn.getHeaderFields());
                logger.d("Http response headers:", headers);
                if (config.isResponseHeadersInLowerCase()) {
                    Map<String, List<String>> headersLowerCase = new HashMap<String, List<String>>();
                    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                        String key = entry.getKey();
                        if (key == null) continue; // ignore the status line whose key is null
                        List<String> values = entry.getValue();
                        headersLowerCase.put(key.toLowerCase(), values);
                        headers = headersLowerCase;
                    }
                }
                return new HttpStreamingResponse(conn, headers);
            } else {
                return new HttpStreamingResponse(conn, (Map<String, List<String>>) null);
            }
        } catch (IOException e) {
            return new HttpStreamingResponse(conn, e);
        }
    }

    protected HttpURLConnection openConnection(String url, HttpMethod method, String contentType) throws IOException {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url + " is not a valid URL", e);
        }
        conn.setConnectTimeout(config.getConnectionTimeout());
        conn.setReadTimeout(config.getReadTimeout());
        conn.setRequestMethod(method.getMethodName());
        conn.setDoOutput(method.isDoOutput());
        conn.setDoInput(method.isDoInput());
        if (config.getDefaultHeaders() != null) {
            for (String name : config.getDefaultHeaders().keySet()) {
                String value = config.getDefaultHeaders().get(name);
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
                try {
                    out.close();
                } catch (Exception ignored) {
                }
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
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }
    }

    private class HttpRequestWorker implements Callable<HttpResponse> {

        private final HTTPClient client;
        private final HttpCallback callback;
        private final HttpRequest request;

        public HttpRequestWorker(HTTPClient httpClient, HttpRequest request) {
            this(httpClient, request, null);
        }

        public HttpRequestWorker(HTTPClient httpClient, HttpRequest request, HttpCallback callback) {
            this.client = httpClient;
            this.request = request;
            this.callback = callback;
        }

        @Override
        public HttpResponse call() throws Exception {
            try {
                HttpResponse resp = client.sendRequestWithRetry(request);
                if (callback != null) {
                    int statusCode = resp.getStatus();
                    if (statusCode >= 100 && statusCode < 200) {
                        callback.onSuccess(resp);
                        callback.onSuccess1xx(resp);
                    } else if (statusCode >= 200 && statusCode < 300) {
                        callback.onSuccess(resp);
                        callback.onSuccess2xx(resp);
                    } else if (statusCode >= 300 && statusCode < 400) {
                        callback.onSuccess(resp);
                        callback.onSuccess3xx(resp);
                    } else if (statusCode >= 400 && statusCode < 500) {
                        callback.onError(resp);
                        callback.onError4xx(resp);
                    } else if (statusCode >= 500) {
                        callback.onError(resp);
                        callback.onError5xx(resp);
                    } else {
                        throw new IllegalArgumentException("Unknown status code:" + statusCode +
                                " for request:" + request.getUrl());
                    }
                }
                return resp;
            } catch (HttpException e) {
                HttpResponse resp = new HttpResponse(e);
                if (e.getCause() instanceof SocketTimeoutException) {
                    callback.onTimeout(resp);
                } else {
                    callback.onError(resp);
                }
                return new HttpResponse(e);
            } catch (Exception e) {
                return new HttpResponse(e);
            }
        }
    }

    public static final class FutureResponse {

        private Future<HttpResponse> future;

        public FutureResponse(Future<HttpResponse> future) {
            this.future = future;
        }

        public Future<HttpResponse> getFuture() {
            return future;
        }

    }

}