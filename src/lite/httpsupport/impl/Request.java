package lite.httpsupport.impl;

import java.util.Map;
import java.util.UUID;

import android.text.TextUtils;

public abstract class Request<T> {

    public final static String DEFAULT_USER_AGENT = "Android";
    public final static int DEFAULT_CONNECT_TIMEOUT = 25 * 1000;
    private final static String DEFAULT_ENCODING = "UTF-8";
    public final static int MAX_RETRY_TIMES = 3;

    public final String uuid;
    public final String url;
    boolean retry;
    int maxRetryTimes = MAX_RETRY_TIMES;

    String userAgent = DEFAULT_USER_AGENT;
    int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    public Request(final String url) {
        this.uuid = UUID.randomUUID().toString();
        this.url = url;
    }

    public Map<String, String> getHeaders() throws Exception {
        return null;
    }

    public final Request<T> setUserAgent(final String userAgent) {
        if (null != userAgent && !TextUtils.isEmpty(userAgent.trim())) {
            this.userAgent = userAgent;
        }
        return this;
    }

    public final Request<T> setConnectTimeout(final int connectTimeout) {
        if (connectTimeout > 0) {
            this.connectTimeout = connectTimeout;
        }
        return this;
    }

    protected String getParamsEncoding() {
        return DEFAULT_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset="
                + getParamsEncoding();
    }

    public byte[] getBody() throws Exception {
        return null;
    }

    abstract protected T parseResponse(final byte[] data) throws Exception;

    public boolean isRetry() {
        return retry;
    }

    public Request<T> setRetry(boolean retry) {
        this.retry = retry;
        return this;
    }

    public Request<T> setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
        return this;
    }

    protected String briefInfo() {
        return "[uuid=" + uuid + ",url=" + url + "]";
    }

    @Override
    public String toString() {
        return super.toString() + ": " + briefInfo();
    }
}