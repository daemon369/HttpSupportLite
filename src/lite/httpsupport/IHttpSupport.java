package lite.httpsupport;

import lite.httpsupport.impl.Request;
import lite.tool.log.ILogger;
import lite.tool.log.ILogger.Level;

public interface IHttpSupport {

    void setThreadPoolFactory(final IThreadPoolFactory factory);

    void setDebug(final boolean debug);

    boolean isDebug();

    Level getLogLevel();

    void setLogLevel(final Level level);

    /**
     * 设置自定义 logger
     * 
     * @param logger
     */
    void setLogger(final ILogger logger);

    /**
     * 发送 post 请求
     * 
     * @param request
     * @param listener
     */
    <T> void post(final Request<T> request, final IHttpListener<T> listener);

    /**
     * 发送 get 请求
     * 
     * @param request
     * @param listener
     */
    <T> void get(final Request<T> request, final IHttpListener<T> listener);
}
