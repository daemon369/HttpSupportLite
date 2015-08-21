package lite.httpsupport;

import lite.httpsupport.codec.ICodec;
import lite.httpsupport.impl.Request;
import lite.httpsupport.log.ILogger;
import lite.httpsupport.log.ILogger.Level;

public interface IHttpSupport {

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
     * 设置是否开启重试模式
     * 
     * @param retry
     */
    void setRetry(final boolean retry);

    /**
     * 设置最大重试次数，需要开启重试模式才能生效
     * 
     * @see #setRetry(boolean)
     * @param maxRetrytimes
     */
    void setMaxRetryTimes(final int maxRetrytimes);

    /**
     * 发送 post 请求
     * 
     * @param request
     * @param listener
     */
    <RESP> void post(final Request request, final IHttpListener<RESP> listener);

    /**
     * 发送 post 请求
     * 
     * @param url
     * @param data
     * @param clz
     * @param codec
     * @param listener
     */
    <RESP> void post(final String url, final Object data, final Class<?> clz,
            final ICodec codec, final IHttpListener<RESP> listener);

    /**
     * 发送 get 请求
     * 
     * @param request
     * @param listener
     */
    <RESP> void get(final Request request, final IHttpListener<RESP> listener);

    /**
     * 发送 get 请求
     * 
     * @param url
     * @param clz
     * @param codec
     * @param listener
     */
    <RESP> void get(final String url, final Class<?> clz, final ICodec codec,
            final IHttpListener<RESP> listener);
}
