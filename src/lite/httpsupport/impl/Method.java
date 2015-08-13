package lite.httpsupport.impl;

enum Method {
    POST("POST"), GET("GET");

    private String method;

    Method(final String method) {
        this.method = method;
    }

    public String getMethod() {
        return this.method;
    }
}
