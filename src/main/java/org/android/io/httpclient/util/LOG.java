package org.android.io.httpclient.util;

import android.util.Log;

/**
 * @author Daniel
 */
public class LOG {

    private String tag;
    // You can use ant to disable logging for your production release, I suggest you keep it on for debug versions
    private static final boolean LOGGING = true;

    public static LOG get(Class<?> clazz) {
        LOG logger = new LOG();
        logger.tag = clazz.getName();
        return logger;
    }

    public void d(String msg) {
        if (LOGGING) {
            Log.d(tag, msg);
        }
    }

    public void d(Object... msgs) {
        if (LOGGING) {
            StringBuilder log = new StringBuilder();
            for (Object msg : msgs) {
                if (msg == null)
                    continue;
                log.append(msg.toString());
            }
            Log.d(tag, log.toString());
        }
    }

    public void i(String msg) {
        if (LOGGING) {
            Log.i(tag, msg);
        }
    }

    public void i(Object... msgs) {
        if (LOGGING) {
            StringBuilder log = new StringBuilder();
            for (Object msg : msgs) {
                if (msg == null)
                    continue;
                log.append(msg.toString());
            }
            Log.i(tag, log.toString());
        }
    }

    public void w(String msg) {
        if (LOGGING) {
            Log.w(tag, msg);
        }
    }

    public void w(String tag, String msg, Throwable ex) {
        if (LOGGING) {
            Log.w(tag, msg, ex);
        }
    }

    public void w(Object... msgs) {
        if (LOGGING) {
            StringBuilder log = new StringBuilder();
            for (Object msg : msgs) {
                if (msg == null)
                    continue;
                log.append(msg.toString());
            }
            Log.w(tag, log.toString());
        }
    }

    public void e(String msg) {
        if (LOGGING) {
            Log.e(tag, msg);
        }
    }

    public void e(Throwable ex, String msg) {
        if (LOGGING) {
            Log.e(tag, msg, ex);
        }
    }

    public void e(Throwable ex, Object... msgs) {
        if (LOGGING) {
            StringBuilder log = new StringBuilder();
            for (Object msg : msgs) {
                if (msg == null)
                    continue;
                log.append(msg.toString());
                log.append("\nstack:\n");
            }
            Log.e(tag, log.toString(), ex);
        }
    }

    public void e(Object... msgs) {
        if (LOGGING) {
            StringBuilder log = new StringBuilder();
            for (Object msg : msgs) {
                if (msg == null)
                    continue;
                log.append(msg.toString());
            }
            Log.e(tag, log.toString());
        }
    }

}