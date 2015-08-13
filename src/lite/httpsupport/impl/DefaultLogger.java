package lite.httpsupport.impl;

import lite.httpsupport.log.ILogger;
import android.util.Log;

class DefaultLogger implements ILogger {
    private boolean debug = true;

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    @Override
    public void v(String tag, String msg) {
        if (debug) {
            Log.v(tag, msg);
        }
    }

    public void v(String tag, String msg, Throwable tr) {
        if (debug) {
            Log.v(tag, msg, tr);
        }
    }

    public void d(String tag, String msg) {
        if (debug) {
            Log.d(tag, msg);
        }
    }

    public void d(String tag, String msg, Throwable tr) {
        if (debug) {
            Log.d(tag, msg, tr);
        }
    }

    public void i(String tag, String msg) {
        if (debug) {
            Log.i(tag, msg);
        }
    }

    public void i(String tag, String msg, Throwable tr) {
        if (debug) {
            Log.i(tag, msg, tr);
        }
    }

    public void w(String tag, String msg) {
        if (debug) {
            Log.w(tag, msg);
        }
    }

    public void w(String tag, String msg, Throwable tr) {
        if (debug) {
            Log.w(tag, msg, tr);
        }
    }

    public void w(String tag, Throwable tr) {
        if (debug) {
            Log.w(tag, tr);
        }
    }

    public void e(String tag, String msg) {
        if (debug) {
            Log.e(tag, msg);
        }
    }

    public void e(String tag, String msg, Throwable tr) {
        if (debug) {
            Log.e(tag, msg, tr);
        }
    }
}
