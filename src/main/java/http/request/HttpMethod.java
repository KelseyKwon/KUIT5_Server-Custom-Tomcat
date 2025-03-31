package http.request;

import http.ExceptionMessage;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS;

    public static HttpMethod from(String httpMethod) {
        for (HttpMethod method : values()) {
            if (method.toString().equalsIgnoreCase(httpMethod)) {
                return method;
            }
        }
        throw new IllegalArgumentException(ExceptionMessage.INVALID_METHOD_TYPE.getExceptionMessage());
    }
}
