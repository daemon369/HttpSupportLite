package lite.httpsupport.impl;

public class HttpError extends Throwable {

    private static final long serialVersionUID = -9096737856064739964L;

    private static final String TAG = "HttpError";

    private int httpCode;
    private int errorCode;
    private String errorMessage;

    public HttpError() {
        super();
    }

    public HttpError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public HttpError(String detailMessage) {
        super(detailMessage);
    }

    public HttpError(Throwable throwable) {
        super(throwable);
    }

    public int getHttpCode() {
        return httpCode;
    }

    public HttpError setHttpCode(int code) {
        this.httpCode = code;
        return this;
    }

    public HttpError setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public HttpError setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void printStackTrace() {
        LogUtils.e(TAG, "[httpCode:" + httpCode + ", errorCode:" + errorCode
                + ", errorMessage:" + errorMessage + "]");
        super.printStackTrace();
    }
}