package lite.httpsupport.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import lite.tool.log.LogUtils;

public class PostTask<T> extends HttpTask<T> {

    private static final String TAG = "PostTask";

    public PostTask(Request<T> request) {
        super(request);
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected void request(final HttpURLConnection conn,
            final Request<T> request) throws HttpError {

        // send data
        final byte[] body;
        try {
            body = request.getBody();
        } catch (Exception e) {
            throw new HttpError(e).setErrorMessage("get body error");
        }

        if (null != body) {
            conn.setDoOutput(true);
            conn.addRequestProperty(HEADER_CONTENT_TYPE,
                    request.getBodyContentType());

            final OutputStream out;
            try {
                out = conn.getOutputStream();
                LogUtils.d(TAG, "output stream is ready");
            } catch (IOException e) {
                throw new HttpError(e).setErrorMessage("net error");
            }

            try {
                out.write(body);
            } catch (IOException e) {
                throw new HttpError(e).setErrorMessage("write stream error");
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }

        }

    }

}
