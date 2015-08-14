package lite.httpsupport.impl;

import static lite.httpsupport.impl.LogUtils.debug;
import static lite.httpsupport.impl.LogUtils.getLogLevel;
import lite.httpsupport.log.ILogger;
import android.util.Log;

class DefaultLogger implements ILogger {

    @Override
    public void v(String tag, String msg) {
        if (debug || getLogLevel().ordinal() == Level.VERBOSE.ordinal()) {
            Log.v(tag, msg);
        }
    }

    public void v(String tag, String msg, Throwable tr) {
        if (debug || getLogLevel().ordinal() == Level.VERBOSE.ordinal()) {
            Log.v(tag, msg, tr);
        }
    }

    public void d(String tag, String msg) {
        if (debug || getLogLevel().ordinal() <= Level.DEBUG.ordinal()) {
            Log.d(tag, msg);
        }
    }

    public void d(String tag, String msg, Throwable tr) {
        if (debug || getLogLevel().ordinal() <= Level.DEBUG.ordinal()) {
            Log.d(tag, msg, tr);
        }
    }

    public void i(String tag, String msg) {
        if (debug || getLogLevel().ordinal() <= Level.INFO.ordinal()) {
            Log.i(tag, msg);
        }
    }

    public void i(String tag, String msg, Throwable tr) {
        if (debug || getLogLevel().ordinal() <= Level.INFO.ordinal()) {
            Log.i(tag, msg, tr);
        }
    }

    public void w(String tag, String msg) {
        if (debug || getLogLevel().ordinal() <= Level.WARN.ordinal()) {
            Log.w(tag, msg);
        }
    }

    public void w(String tag, String msg, Throwable tr) {
        if (debug || getLogLevel().ordinal() <= Level.WARN.ordinal()) {
            Log.w(tag, msg, tr);
        }
    }

    public void w(String tag, Throwable tr) {
        if (debug || getLogLevel().ordinal() <= Level.WARN.ordinal()) {
            Log.w(tag, tr);
        }
    }

    public void e(String tag, String msg) {
        if (debug || getLogLevel().ordinal() <= Level.ERROR.ordinal()) {
            Log.e(tag, msg);
        }
    }

    public void e(String tag, String msg, Throwable tr) {
        if (debug || getLogLevel().ordinal() <= Level.ERROR.ordinal()) {
            Log.e(tag, msg, tr);
        }
    }
}
