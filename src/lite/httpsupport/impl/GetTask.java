package lite.httpsupport.impl;

import java.net.HttpURLConnection;

public class GetTask<T> extends HttpTask<T> {

    public GetTask(Request<T> request) {
        super(request);
    }

    @Override
    protected Method getMethod() {
        return Method.GET;
    }

    @Override
    protected void request(HttpURLConnection conn, Request<T> request)
            throws HttpError {
        // empty
    }

}
