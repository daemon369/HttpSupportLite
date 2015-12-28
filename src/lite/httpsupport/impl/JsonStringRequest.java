package lite.httpsupport.impl;

public class JsonStringRequest extends StringRequest {
    // private static final String TAG = "JsonStringRequest";

    public JsonStringRequest(String url, final String request) {
        super(url, request);
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }
}
