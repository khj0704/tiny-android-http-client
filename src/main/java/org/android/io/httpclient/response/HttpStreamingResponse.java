package org.android.io.httpclient.response;

import com.meiyugames.log.LOG;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel
 *         If you want to download a large file you need this class.
 *         Note, you are responsible for closing the http connection by calling the <code>close()</code> method
 */
public class HttpStreamingResponse extends HttpResponse {

    private static final LOG logger = LOG.get(HttpStreamingResponse.class);

    private final HttpURLConnection conn;
    private InputStream inStream;
    private InputStream errStream;

    public HttpStreamingResponse(HttpURLConnection conn, Exception ex) {
        super(ex);
        this.conn = conn;
        this.inStream = null;
        this.errStream = conn.getErrorStream();
        logger.i(ex, "Streaming response from url:", conn.getURL().toString(), ", exception=", ex.getClass(), ":",
                ex.getMessage());
    }

    public HttpStreamingResponse(HttpURLConnection conn, Map<String, List<String>> headers) throws IOException {
        super(conn.getResponseCode(), conn.getURL().toString(), headers, null);
        this.conn = conn;
        try {
            this.inStream = conn.getInputStream();
        } catch (Exception ex) {
            logger.e(ex);
            this.inStream = null;
        }
        try {
            this.errStream = conn.getErrorStream();
        } catch (Exception ex) {
            logger.e(ex);
            errStream = null;
        }
        logger.i("Streaming response from url:", conn.getURL().toString());
    }

    public InputStream getInputStream() {
        return inStream;
    }

    public InputStream getErrStream() {
        return errStream;
    }

    /**
     * Only for small response!
     *
     * @return
     */
    @Override
    public String getBodyString() {
        synchronized (this) {
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            try {
                if (body == null && inStream != null) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = inStream.read(buf)) >= 0) {
                        temp.write(buf, 0, len);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); // TODO: logger
            } finally {
                try {
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            body = temp.toByteArray();
        }

        try {
            if (body != null) {
                return new String(body, "utf8");
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return "";
    }

    public void close() throws IOException {
        if (inStream != null) {
            inStream.close();
        }
        if (errStream != null) {
            errStream.close();
        }
        if (conn != null) {
            conn.disconnect();
        }
    }

}
