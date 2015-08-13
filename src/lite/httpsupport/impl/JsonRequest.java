package lite.httpsupport.impl;

import lite.httpsupport.codec.ICodec;

public class JsonRequest extends Request {
    public JsonRequest() {
        this.codec = new JsonCodec();
    }

    @Override
    public JsonRequest setCodec(ICodec codec) {
        return this;
    }
}
