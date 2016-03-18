package lite.httpsupport.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lite.httpsupport.IThreadPoolFactory;

public class ThreadPoolFactory implements IThreadPoolFactory {

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    @Override
    public ThreadPoolExecutor newExecutor() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(30));
    }

}
