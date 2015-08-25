package lite.httpsupport;

import lite.httpsupport.impl.HttpError;

public interface IHttpListener<T> {

    void onSuccess(final T rsp);

    void onFail(final HttpError error);
}