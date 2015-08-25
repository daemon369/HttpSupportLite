package lite.httpsupport.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class PostTask<T> extends HttpTask<T> {

    private static final String TAG = "PostTask";

    public PostTask(Request<T> request) {
        super(request);
    }

    @Override
    protected Method getMethod() {
        return Method.POST;
    }

    @Override
    protected void request(final HttpURLConnection conn,
            final Request<T> request) throws HttpError {

        // send data
        final byte[] body;
        try {
            body = request.getBody();
        } catch (Exception e) {
            throw new HttpError(e).setErrorMessage("request.getBody exception");
        }

        if (null != body) {
            conn.setDoOutput(true);
            conn.addRequestProperty(HEADER_CONTENT_TYPE,
                    request.getBodyContentType());

            final OutputStream out;
            try {
                out = conn.getOutputStream();
                LogUtils.d(TAG, "打开 OutputStream 准备发送数据");
            } catch (IOException e) {
                throw new HttpError(e).setErrorMessage("OutputStream打开失败："
                        + e.getMessage());
            }

            try {
                out.write(body);
            } catch (IOException e) {
                throw new HttpError(e).setErrorMessage("发送请求IO异常："
                        + e.getMessage());
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }

        }

    }

}
