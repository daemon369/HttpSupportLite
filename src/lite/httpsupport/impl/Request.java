package lite.httpsupport.impl;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public abstract class Request<T> {
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static final int MAX_RETRY_TIMES = 3;

    final String uuid;
    final String url;
    boolean retry;
    int maxRetryTimes = MAX_RETRY_TIMES;
    Object tag;

    public Request(final String url) {
        this.uuid = UUID.randomUUID().toString();
        this.url = url;
    }

    public Map<String, String> getHeaders() throws Exception {
        return Collections.emptyMap();
    }

    protected String getParamsEncoding() {
        return DEFAULT_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public byte[] getBody() throws Exception {
        return null;
    }

    abstract protected T parseResponse(final byte[] data) throws Exception;

    protected HttpError parseNetworkError(HttpError httpError) {
        return httpError;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUrl() {
        return url;
    }

    public boolean isRetry() {
        return retry;
    }

    public Request<T> setRetry(boolean retry) {
        this.retry = retry;
        return this;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public Request<T> setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public Request<T> setTag(Object tag) {
        this.tag = tag;
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