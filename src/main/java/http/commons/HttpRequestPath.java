package http.commons;

import http.ExceptionMessage;

public enum HttpRequestPath {
    HOME("/", "/index.html"),
    SIGNUP("/user/signup", "/user/form.html"),
    LOGIN("/user/login", "/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html", "/user/login_failed.html"),
    USER_LIST("/user/userList", "/user/list.html");

    private final String url;
    private final String staticFilePath;

    HttpRequestPath(String url, String staticFilePath) {
        this.url = url;
        this.staticFilePath = staticFilePath;
    }

    public String getUrl() {
        return url;
    }

    public String getStaticFilePath() {
        return staticFilePath;
    }

    public static HttpRequestPath from(String requestedUrl) {
        for (HttpRequestPath httpRequestPath : values()) {
            if (httpRequestPath.url.equals(requestedUrl)) {
                return httpRequestPath;
            }
        }
        throw new IllegalArgumentException(ExceptionMessage.INVALID_REQUESTED_URL.getExceptionMessage());
    }
}
