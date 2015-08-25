package lite.httpsupport.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

public class JsonRequest extends Request<JSON> {
    private final Object request;

    public JsonRequest(String url, final Object request) {
        super(url);
        this.request = request;
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public byte[] getBody() throws Exception {
        if (null == request) {
            return null;
        }

        return JSON.toJSONString(request).getBytes();
    }

    @Override
    protected JSON parseResponse(byte[] data) throws Exception {
        final String jsonStr = new String(data);
        if (!TextUtils.isEmpty(jsonStr)) {
            return (JSON) JSON.parse(jsonStr);
        }

        return null;
    }
}
