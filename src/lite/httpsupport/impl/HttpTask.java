package lite.httpsupport.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import lite.httpsupport.IHttpListener;
import lite.httpsupport.codec.ICodec.CodecException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

abstract class HttpTask<RESP> implements Runnable {
    private static final String TAG = "HttpTask";

    private Request request;
    private IHttpListener<RESP> listener;
    private byte[] recvBuffer;
    private int retryTimes = 0;

    private final static String CONTENT_TYPE = "application/json";
    private final static String USER_AGENT = "Android";
    private final static String PRAGMA = "no-cache";
    private final static String ACCEPT_LANGUAGE = "zh-CN";
    private final static String ACCEPT = "*/*";
    private final static int CONNECTION_TIMEOUT = 25 * 1000;
    private static final int READWIRTE_TIMEOUT = 30 * 1000;

    private static final int WHAT_SUCCESS = 1;
    private static final int WHAT_FAILED = 2;

    private Handler handler = null;
    private Handler.Callback callback = new Handler.Callback() {

        @SuppressWarnings("unchecked")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case WHAT_SUCCESS:
                try {
                    listener.onSuccess((RESP) msg.obj);
                } catch (Exception e) {
                    LogUtils.e(TAG, "handle success exception:", e);
                }
                break;

            case WHAT_FAILED:
                try {
                    listener.onFail((HttpError) msg.obj);
                } catch (Exception e) {
                    LogUtils.e(TAG, "handle failed exception", e);
                }
                break;

            default:
                break;
            }

            return true;
        }
    };

    public HttpTask() {
        this.recvBuffer = new byte[1024 * 8];
        handler = new Handler(Looper.getMainLooper(), callback);
    }

    public HttpTask<RESP> setRequest(final Request request) {
        this.request = request;
        return this;
    }

    public HttpTask<RESP> setHttpListener(final IHttpListener<RESP> listener) {
        this.listener = listener;
        return this;
    }

    protected void setRequestProperty(final HttpURLConnection conn) {
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READWIRTE_TIMEOUT);
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Content-Type", CONTENT_TYPE);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Pragma", PRAGMA);
        conn.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
        conn.setRequestProperty("Accept", ACCEPT);
    }

    @Override
    public void run() {
        LogUtils.d(TAG, "request: " + request + " listener: " + listener);

        try {
            if (null == request) {
                throw new HttpError().setErrorMessage("请求为空：" + request);
            }

            final URL url;
            try {
                url = new URL(request.getUrl());
                LogUtils.d(TAG, "url: " + url);
            } catch (MalformedURLException e) {
                throw new HttpError(e).setErrorMessage("URL非法："
                        + request.getUrl() + " " + e.getMessage());
            }

            while (true) {
                try {
                    tryRequest(url);
                    // 请求完成，返回
                    return;

                } catch (HttpError err) {
                    if (!request.isRetry()) {
                        LogUtils.e(TAG, "请求失败，不进行重试");
                        throw err;
                    } else if (retryTimes++ >= request.getMaxRetryTimes()) {
                        // 超过最大重试次数
                        LogUtils.e(TAG,
                                "超过最大重试次数：" + request.getMaxRetryTimes());
                        throw err;
                    } else {
                        LogUtils.w(
                                TAG,
                                "第[" + retryTimes + "]次重试："
                                        + err.getErrorMessage());
                    }
                }
            }

        } catch (HttpError err) {
            // 请求失败或返回不成功
            LogUtils.w(TAG, "请求失败：", err);
            handleFailed(request.getThreadMode(), err);

        } catch (Exception e) {
            // 请求失败或返回不成功
            LogUtils.e(TAG, "请求失败，未正确处理的异常：", e);

            final HttpError err = new HttpError(e).setErrorMessage("未正确处理的异常："
                    + e.getMessage());
            handleFailed(request.getThreadMode(), err);
        }
    }

    private void tryRequest(final URL url) throws HttpError {
        // open connection
        final HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
            LogUtils.v(TAG, "url.openConnection 成功");
        } catch (IOException e) {
            throw new HttpError(e).setErrorMessage("打开Http连接失败："
                    + e.getMessage());
        }

        try {
            try {
                conn.setRequestMethod(getMethod().getMethod());
            } catch (NullPointerException e) {
                throw new HttpError().setErrorMessage("HTTP method 不能为空！");
            } catch (ProtocolException e) {
                throw new HttpError(e).setErrorMessage("设置Http Method异常");
            }

            setRequestProperty(conn);

            try {
                conn.connect();
            } catch (IOException e) {
                throw new HttpError(e).setErrorMessage("Http连接错误："
                        + e.getMessage());
            }

            request(conn, request);

            // receive data
            final int code;
            try {
                code = conn.getResponseCode();
            } catch (IOException e) {
                throw new HttpError(e).setErrorMessage("获取返回码失败："
                        + e.getMessage());
            }

            LogUtils.d(TAG, "response code:" + code);

            if (code != HttpURLConnection.HTTP_OK
                    && code != HttpURLConnection.HTTP_PARTIAL) {
                throw new HttpError().setHttpCode(code).setErrorMessage(
                        "返回码为：" + code);
            }

            final InputStream in;
            try {
                in = conn.getInputStream();
            } catch (IOException e) {
                throw new HttpError(e).setHttpCode(code).setErrorMessage(
                        "打开InputStream失败：" + e.getMessage());
            }

            try {
                final ByteArrayOutputStream saveStream = new ByteArrayOutputStream();

                try {
                    int len = -1;

                    try {
                        while ((len = in.read(recvBuffer)) != -1) {
                            LogUtils.d(TAG, "receive length:" + len);
                            saveStream.write(recvBuffer, 0, len);
                        }
                    } catch (IOException e) {
                        throw new HttpError(e).setHttpCode(code)
                                .setErrorMessage("读取数据失败：" + e.getMessage());
                    }

                    final byte[] responseBuff = saveStream.toByteArray();
                    LogUtils.d(TAG, "response length:" + responseBuff.length);

                    try {
                        LogUtils.d(TAG, "RESP class:" + request.getClz());
                        final RESP obj = request.getCodec().decode(
                                responseBuff, request.getClz());

                        if (null != obj) {
                            handleSuccess(request.getThreadMode(), obj);
                        } else {
                            throw new HttpError().setHttpCode(code)
                                    .setErrorMessage("解码返回 null");
                        }
                    } catch (CodecException e) {
                        throw new HttpError(e).setHttpCode(code)
                                .setErrorMessage("解码失败");
                    }

                } finally {
                    try {
                        saveStream.close();
                    } catch (IOException e) {
                    }
                }

            } finally {
                // 关闭 InputStream
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        } finally {
            // 关闭 http 连接
            conn.disconnect();
        }
    }

    protected abstract Method getMethod();

    protected abstract void request(final HttpURLConnection conn,
            final Request request) throws HttpError;

    protected void handleSuccess(final ThreadMode mode, final RESP o) {
        switch (mode) {
        case Default:
            try {
                listener.onSuccess(o);
            } catch (Exception e) {
                LogUtils.e(TAG, "handle success exception:", e);
            }
            break;

        default:
            final Message msg = handler.obtainMessage(WHAT_SUCCESS, o);
            handler.sendMessage(msg);
            break;
        }
    }

    protected void handleFailed(final ThreadMode mode, final HttpError err) {
        switch (mode) {
        case Default:
            try {
                listener.onFail(err);
            } catch (Exception e) {
                LogUtils.e(TAG, "handle failed exception", e);
            }
            break;

        default:
            handler.sendMessage(handler.obtainMessage(WHAT_FAILED, err));
            break;
        }
    }
}