package lite.httpsupport;

import java.util.concurrent.ThreadPoolExecutor;

public interface IThreadPoolFactory {

    ThreadPoolExecutor newExecutor();

}
