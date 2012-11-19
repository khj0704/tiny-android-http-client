package org.android.http.client.cookie;

import android.os.Build;

import java.net.CookieHandler;

/**
 * @author: Daniel
 */
public class DurableCookieManager implements CookieManager {

    public DurableCookieManager() {
        if (Build.VERSION.SDK_INT > 8) {
            if (CookieHandler.getDefault() == null) {
                CookieHandler.setDefault(new java.net.CookieManager());
            }
        } else {

        }
    }

}
