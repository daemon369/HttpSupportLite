package lite.httpsupport.impl;

import lite.httpsupport.codec.ICodec;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

class JsonCodec implements ICodec {
    private static final String TAG = "JsonCodec";

    private volatile static JsonCodec instance = null;

    public static JsonCodec getDefault() {
        if (null == instance) {
            synchronized (JsonCodec.class) {
                if (null == instance) {
                    instance = new JsonCodec();
                }
            }
        }

        return instance;
    }

    @Override
    public byte[] encode(Object data) throws CodecException {
        if (null == data) {
            LogUtils.e(TAG, "请求实体为 null");
            throw new CodecException("请求实体为 null").setType(Type.ENCODE);
        }

        final String json = JSONObject.toJSONString(data);
        if (TextUtils.isEmpty(json)) {
            LogUtils.e(TAG, "请求为空：" + json);
            throw new CodecException("请求为空：" + json).setType(Type.ENCODE);
        }

        final byte[] bytes = json.getBytes();

        LogUtils.e(TAG, "编码后数据长度：" + bytes.length);
        return bytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <RESP> RESP decode(byte[] data, final Class<?> clz)
            throws CodecException {
        if (null == data || 0 == data.length) {
            LogUtils.e(TAG, "响应数据为空：" + data);
            throw new CodecException("响应数据为空：" + data).setType(Type.DECODE);
        }

        final String str = new String(data);
        if (TextUtils.isEmpty(str)) {
            LogUtils.e(TAG, "响应数据为空：" + str);
            throw new CodecException("响应数据为空：" + str).setType(Type.DECODE);
        }

        try {
            final Object rsp = JSONObject.parseObject(str, clz);

            LogUtils.e(TAG, "解码后响应实体：" + rsp);
            return (RESP) rsp;
        } catch (Exception e) {
            LogUtils.e(TAG, "解码错误：" + str);
            throw new CodecException("解码错误：" + str).setType(Type.DECODE);
        }
    }
}