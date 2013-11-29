package org.android.io.httpclient;

import com.jijiagames.gamecenter.AppContent;
import com.jijiagames.gamecenter.constdefine.PersistentKey;

import java.util.Map;

/**
 * @author Daniel
 */
public class HTTPClientFactory {

    private volatile static HTTPClient client;

    public static HTTPClient get() {
        if (client == null) {
            synchronized (HTTPClientFactory.class) {
                if (client == null) {
                    HTTPClientConfig config = new HTTPClientConfig();
                    config.setResponseHeadersInLowerCase(true);
                    client = new HTTPClient(config);
                    Map<String, String> headers = client.getConfig().getDefaultHeaders();
                    headers.put("vc", String.valueOf(AppContent.getInstance().getVersionCode()));
                    headers.put("x-client-type", PersistentKey.CLIENT_TYPE_GC);
                }
            }
        }
        return client;
    }

}
