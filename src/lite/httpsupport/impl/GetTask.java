package lite.httpsupport.impl;

import java.net.HttpURLConnection;

public class GetTask<RESP> extends HttpTask<RESP> {

    @Override
    protected Method getMethod() {
        return Method.GET;
    }

    @Override
    protected void request(HttpURLConnection conn, Request request)
            throws HttpError {
        // empty
    }

}
