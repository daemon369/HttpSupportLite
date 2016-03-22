package lite.httpsupport.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lite.httpsupport.IThreadPoolFactory;
import lite.tool.log.LogUtils;

public class ThreadPoolFactory implements IThreadPoolFactory {

    private static final String TAG = "ThreadPoolFactory";

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    @Override
    public ThreadPoolExecutor newExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(30),
                new RejectedHandler());
    }

    private static class RejectedHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof HttpTask<?>) {
                final HttpTask<?> task = (HttpTask<?>) r;
                try {
                    task.getHttpListener().onFail(
                            new HttpError("Task " + r.toString()
                                    + " rejected from " + executor.toString()));
                } catch (Exception e) {
                    LogUtils.w(TAG, "handle fail exception", e);
                }
            } else {
                LogUtils.w(TAG, "unexpected runnable type: " + r.toString());
            }
        }

    }
}
