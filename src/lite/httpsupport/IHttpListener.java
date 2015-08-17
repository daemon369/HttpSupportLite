package lite.httpsupport;

import lite.httpsupport.impl.HttpError;

public interface IHttpListener<RESP> {

    void onSuccess(final RESP rsp);

    void onFail(final HttpError error);
}