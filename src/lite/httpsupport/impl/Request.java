package lite.httpsupport.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

public abstract class Request<T> {
    private static final String DEFAULT_ENCODING = "UTF-8";

    final String url;
    boolean retry;
    int maxRetryTimes;
    Object tag;

    public Request(final String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() throws Exception {
        return Collections.emptyMap();
    }

    protected Map<String, String> getParams() throws Exception {
        return null;
    }

    protected String getParamsEncoding() {
        return DEFAULT_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset="
                + getParamsEncoding();
    }

    public byte[] getBody() throws Exception {
        final Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params,
            String paramsEncoding) {
        final StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(),
                        paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(),
                        paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: "
                    + paramsEncoding, uee);
        }
    }

    abstract protected T parseResponse(final byte[] data) throws Exception;

    protected HttpError parseNetworkError(HttpError httpError) {
        return httpError;
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
}