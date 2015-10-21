package lite.httpsupport.impl;

import java.net.HttpURLConnection;

public class GetTask<T> extends HttpTask<T> {

    public GetTask(Request<T> request) {
        super(request);
    }

    @Override
    protected String getMethod() {
        return "GET";
    }

    @Override
    protected void request(HttpURLConnection conn, Request<T> request)
            throws HttpError {
        // empty
    }

}
