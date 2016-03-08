package lite.httpsupport.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import lite.httpsupport.IHttpListener;
import lite.tool.log.LogUtils;

abstract class HttpTask<T> implements Runnable {
    private static final String TAG = "HttpTask";

    private final Request<T> request;
    private IHttpListener<T> listener;
    private byte[] recvBuffer;
    private int retryTimes = 0;

    private final static String PRAGMA = "no-cache";
    private final static String ACCEPT_LANGUAGE = "zh-CN";
    private final static String ACCEPT = "*/*";
    private final static int CONNECTION_TIMEOUT = 25 * 1000;
    private static final int READWIRTE_TIMEOUT = 30 * 1000;

    protected static final String HEADER_CONTENT_TYPE = "Content-Type";

    public HttpTask(final Request<T> request) {
        this.recvBuffer = new byte[1024 * 8];
        this.request = request;
    }

    public HttpTask<T> setHttpListener(final IHttpListener<T> listener) {
        this.listener = listener;
        return this;
    }

    protected void setRequestProperty(final HttpURLConnection conn) {
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READWIRTE_TIMEOUT);
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Pragma", PRAGMA);
        conn.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
        conn.setRequestProperty("Accept", ACCEPT);
    }

    @Override
    public void run() {
        LogUtils.d(TAG, request + " listener: " + listener);

        try {
            if (null == request) {
                throw new HttpError().setErrorMessage("request is null");
            }

            final URL url;
            try {
                url = new URL(request.url);
                LogUtils.d(TAG, "url: " + url);
            } catch (MalformedURLException e) {
                throw new HttpError(e).setErrorMessage("URL非法：" + request.url
                        + " " + e.getMessage());
            }

            while (true) {
                try {
                    tryRequest(url);
                    // 请求完成，返回
                    return;

                } catch (HttpError err) {
                    if (!request.isRetry()) {
                        LogUtils.e(
                                TAG,
                                request + " 请求失败，不进行重试："
                                        + err.getErrorMessage());
                        throw err;
                    } else if (retryTimes++ >= request.maxRetryTimes) {
                        // 超过最大重试次数
                        LogUtils.e(TAG,
                                request + " 请求失败：" + err.getErrorMessage()
                                        + " 超过最大重试次数：" + request.maxRetryTimes);
                        throw err;
                    } else {
                        LogUtils.w(TAG, request + " 第[" + retryTimes + "]次重试："
                                + err.getErrorMessage());
                    }
                }
            }

        } catch (HttpError err) {
            // 请求失败或返回不成功
            LogUtils.w(TAG, request + " 请求失败：", err);
            handleFailed(err);

        } catch (Exception e) {
            // 请求失败或返回不成功
            LogUtils.e(TAG, request + " 请求失败，未正确处理的异常：", e);

            final HttpError err = new HttpError(e).setErrorMessage("未正确处理的异常："
                    + e.getMessage());
            handleFailed(err);
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

        final String method = getMethod();
        if (TextUtils.isEmpty(method)) {
            throw new HttpError().setErrorMessage("HTTP method 不能为空！");
        }

        try {
            try {
                conn.setRequestMethod(method);
            } catch (ProtocolException e) {
                throw new HttpError(e).setErrorMessage("设置Http Method异常");
            }

            final HashMap<String, String> map = new HashMap<String, String>();
            try {
                final Map<String, String> headers = request.getHeaders();
                if (null != headers) {
                    map.putAll(request.getHeaders());
                }
            } catch (Exception e) {
                LogUtils.w(TAG, e);
            }

            for (String headerName : map.keySet()) {
                conn.addRequestProperty(headerName, map.get(headerName));
            }

            LogUtils.d(TAG, "user agent: " + request.userAgent);
            conn.setRequestProperty("User-Agent", request.userAgent);

            setRequestProperty(conn);

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
                        "getInputStream：" + e.getMessage());
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

                    final T response;
                    try {
                        response = request.parseResponse(responseBuff);

                        if (null != response) {
                            handleSuccess(response);
                        } else {
                            throw new HttpError().setHttpCode(code)
                                    .setErrorMessage("解码失败");
                        }
                    } catch (Exception e) {
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

    protected abstract String getMethod();

    protected abstract void request(final HttpURLConnection conn,
            final Request<T> request) throws HttpError;

    protected void handleSuccess(final T o) {
        try {
            listener.onSuccess(o);
        } catch (Exception e) {
            LogUtils.e(TAG, "handle success exception:", e);
        }
    }

    protected void handleFailed(final HttpError err) {
        try {
            listener.onFail(err);
        } catch (Exception e) {
            LogUtils.e(TAG, "handle failed exception", e);
        }
    }
}