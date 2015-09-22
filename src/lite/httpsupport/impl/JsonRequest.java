package lite.httpsupport.impl;

import lite.tool.log.LogUtils;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

public class JsonRequest extends Request<JSON> {
    private static final String TAG = "JsonRequest";
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
            LogUtils.w(TAG, briefInfo() + " request is null");
            return null;
        }

        final String json = JSON.toJSONString(request);

        LogUtils.d(TAG, briefInfo() + " json:" + json);

        return json.getBytes();
    }

    @Override
    protected JSON parseResponse(byte[] data) throws Exception {
        final String json = new String(data);

        LogUtils.d(TAG, briefInfo() + " response json:" + json);

        if (!TextUtils.isEmpty(json)) {
            return (JSON) JSON.parse(json);
        }

        return null;
    }
}
