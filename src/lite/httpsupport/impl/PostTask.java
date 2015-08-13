package lite.httpsupport.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import lite.httpsupport.codec.ICodec.CodecException;

public class PostTask<RESP> extends HttpTask<RESP> {

    private static final String TAG = "PostTask";

    @Override
    protected Method getMethod() {
        return Method.POST;
    }

    @Override
    protected void request(final HttpURLConnection conn, final Request request)
            throws HttpError {

        // send data
        OutputStream out;
        try {
            out = conn.getOutputStream();
            LogUtils.d(TAG, "打开 OutputStream 准备发送数据");
        } catch (IOException e) {
            throw new HttpError(e).setErrorMessage("OutputStream打开失败："
                    + e.getMessage());
        }

        final byte[] reqData;
        try {
            reqData = request.getCodec().encode(request.getData());
            LogUtils.d(TAG, "请求实体编码成功");
        } catch (CodecException e) {
            throw new HttpError(e).setErrorMessage("编码失败：" + e.getMessage());
        }

        try {
            out.write(reqData);
            out.flush();
        } catch (IOException e) {
            throw new HttpError(e)
                    .setErrorMessage("发送请求IO异常：" + e.getMessage());
        } finally {
            // 关闭 OutputStream
            try {
                out.close();
            } catch (IOException e1) {
            }
        }

    }

}
