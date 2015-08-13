package lite.httpsupport.impl;

import lite.httpsupport.codec.ICodec;

public class Request {
    String url;
    Object data;
    ICodec codec;
    Class<?> clz;
    boolean retry;
    int maxRetryTimes;
    ThreadMode threadMode;

    @Override
    public String toString() {
        return "[url:" + url + ", data:" + data + ", codec:" + codec + ", clz:"
                + clz + ", retry:" + retry + ", maxRetryTimes:" + maxRetryTimes
                + "]";
    }

    public String getUrl() {
        return url;
    }

    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Request setData(Object data) {
        this.data = data;
        return this;
    }

    public ICodec getCodec() {
        return codec;
    }

    public Request setCodec(ICodec codec) {
        this.codec = codec;
        return this;
    }

    public Class<?> getClz() {
        return clz;
    }

    public Request setClz(Class<?> clz) {
        this.clz = clz;
        return this;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public Request setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
        return this;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public Request setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
        return this;
    }
}