package lite.httpsupport.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lite.httpsupport.IHttpListener;
import lite.httpsupport.IHttpSupport;
import lite.httpsupport.codec.ICodec;
import lite.httpsupport.log.ILogger;
import lite.httpsupport.url.IUrlGenerator;

public class HttpSupport implements IHttpSupport {
    static final String TAG = "HttpSupport";

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private volatile static HttpSupport instance = null;
    private volatile ThreadPoolExecutor executor = null;

    private ThreadMode threadMode = ThreadMode.Default;
    private IUrlGenerator urlGenerator = null;

    private boolean retry = true;
    private int maxRetryTimes = 3;

    public HttpSupport() {
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
                    executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(10),
                            new MyThreadFactory());
                }
            }
        }

        return executor;
    }

    @Override
    public void setLogger(ILogger logger) {
        LogUtils.setLogger(logger);
    }

    @Override
    public void setThreadMode(ThreadMode mode) {
        this.threadMode = mode;
    }

    @Override
    public void setUrlGenerator(final IUrlGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

    @Override
    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    @Override
    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    @Override
    public <RESP> void post(Request request, IHttpListener<RESP> listener) {

        final HttpTask<RESP> task = new PostTask<RESP>().setRequest(request)
                .setHttpListener(listener);
        getExecutor().execute(task);
    }

    @Override
    public <RESP> void postJson(String cmd, Object data, final Class<?> clz,
            IHttpListener<RESP> listener) {
        check();

        final JsonRequest request = new JsonRequest();
        request.setClz(clz);
        request.setThreadMode(threadMode);
        request.setUrl(urlGenerator.toUrl(cmd));
        request.setData(data);
        request.setRetry(retry);
        request.setMaxRetryTimes(maxRetryTimes);

        final HttpTask<RESP> task = new PostTask<RESP>().setRequest(request)
                .setHttpListener(listener);
        getExecutor().execute(task);
    }

    @Override
    public <RESP> void post(String cmd, Object data, final Class<?> clz,
            ICodec codec, IHttpListener<RESP> listener) {
        check();

        final Request request = new Request();
        request.setCodec(codec);
        request.setClz(clz);
        request.setThreadMode(threadMode);
        request.setUrl(urlGenerator.toUrl(cmd));
        request.setData(data);
        request.setRetry(retry);
        request.setMaxRetryTimes(maxRetryTimes);

        final HttpTask<RESP> task = new PostTask<RESP>().setRequest(request)
                .setHttpListener(listener);
        getExecutor().execute(task);
    }

    static class MyThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MyThreadFactory() {
            namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(r, namePrefix
                    + threadNumber.getAndIncrement());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            return t;
        }
    }

    @Override
    public <RESP> void get(Request request, IHttpListener<RESP> listener) {

        final HttpTask<RESP> task = new GetTask<RESP>().setRequest(request)
                .setHttpListener(listener);
        getExecutor().execute(task);
    }

    @Override
    public <RESP> void get(String cmd, Class<?> clz, ICodec codec,
            IHttpListener<RESP> listener) {
        check();

        if (null == clz || null == codec) {
            throw new IllegalArgumentException("参数错误");
        }

        final Request request = new Request();
        request.setCodec(codec);
        request.setClz(clz);
        request.setThreadMode(threadMode);
        request.setUrl(urlGenerator.toUrl(cmd));
        request.setRetry(retry);
        request.setMaxRetryTimes(maxRetryTimes);

        get(request, listener);
    }

    @Override
    public <RESP> void getByUrl(String url, Class<?> clz, ICodec codec,
            IHttpListener<RESP> listener) {
        final Request request = new Request();
        request.setCodec(codec);
        request.setClz(clz);
        request.setThreadMode(threadMode);
        request.setUrl(url);
        request.setRetry(retry);
        request.setMaxRetryTimes(maxRetryTimes);

        get(request, listener);
    }

    private void check() {
        if (null == urlGenerator) {
            throw new IllegalStateException(
                    "URL 产生器不能为空，请调用 setUrlGenerator 方法设置");
        }
    }
}
