package lite.httpsupport.impl;

import lite.tool.log.LogUtils;

public class StringRequest extends Request<String> {
    private static final String TAG = "StringRequest";
    private final String request;

    public StringRequest(String url, final String request) {
        super(url);
        this.request = request;
    }

    @Override
    public byte[] getBody() throws Exception {
        if (null == request) {
            LogUtils.w(TAG, briefInfo() + " request is null");
            return null;
        }

        LogUtils.d(TAG, briefInfo() + " request:" + request);

        return request.getBytes();
    }

    @Override
    protected String parseResponse(byte[] data) throws Exception {
        final String str = new String(data);

        LogUtils.d(TAG, briefInfo() + " response string:" + str);

        return str;
    }
}
