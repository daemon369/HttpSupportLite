package lite.httpsupport.impl;

import java.util.concurrent.ThreadPoolExecutor;

import lite.httpsupport.IHttpListener;
import lite.httpsupport.IHttpSupport;
import lite.httpsupport.IThreadPoolFactory;
import lite.tool.log.ILogger;
import lite.tool.log.ILogger.Level;
import lite.tool.log.LogUtils;

public class HttpSupport implements IHttpSupport {
    static final String TAG = "HttpSupport";

    private volatile static HttpSupport instance = null;
    private IThreadPoolFactory threadPoolFactory;
    private volatile ThreadPoolExecutor executor = null;

    private boolean debug = false;

    public HttpSupport() {
        threadPoolFactory = new ThreadPoolFactory();
    }

    public static HttpSupport getDefault() {
        if (null == instance) {
            synchronized (HttpSupport.class) {
                if (null == instance) {
                    instance = new HttpSupport();
                }
            }
        }

        return instance;
    }

    private ThreadPoolExecutor getExecutor() {
        if (null == executor || executor.isShutdown()) {
            synchronized (this) {
                if (null == executor || executor.isShutdown()) {
                    executor = threadPoolFactory.newExecutor();
                }
            }
        }

        return executor;
    }

    @Override
    public void setThreadPoolFactory(IThreadPoolFactory factory) {
        if (null != factory) {
            this.threadPoolFactory = factory;
        }
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
        LogUtils.debug = debug;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    @Override
    public Level getLogLevel() {
        return LogUtils.getLogLevel();
    }

    @Override
    public void setLogLevel(Level level) {
        LogUtils.setLogLevel(level);
    }

    @Override
    public void setLogger(ILogger logger) {
        LogUtils.setLogger(logger);
    }

    @Override
    public <T> void post(Request<T> request, IHttpListener<T> listener) {
        check(null == request || null == request.url);

        final HttpTask<T> task = new PostTask<T>(request)
                .setHttpListener(listener);
        getExecutor().execute(task);
    }

    @Override
    public <T> void get(Request<T> request, IHttpListener<T> listener) {
        check(null == request || null == request.url);

        final HttpTask<T> task = new GetTask<T>(request)
                .setHttpListener(listener);
        getExecutor().execute(task);
    }

    private void check(final boolean illegal) {
        if (illegal) {
            final IllegalArgumentException e = new IllegalArgumentException(
                    "illegal argument");

            if (debug) {
                throw e;
            } else {
                LogUtils.e(TAG, "check", e);
            }
        }
    }
}
