package http.commons;

import java.util.Map;

import http.ExceptionMessage;

public enum RequestHttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String headerMessage;

    RequestHttpHeader(String headerMessage) {
        this.headerMessage = headerMessage;
    }

    public String getHeaderMessage() {
        return headerMessage;
    }

    public static String findHeaderValues(Map<String, String> headers, RequestHttpHeader targetHeader) {
        return headers.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(targetHeader.headerMessage))
                .findFirst()
                .map(headers::get)
                .orElse(null);
    }

    public static int getContentLength(Map<String, String> headers) {
        String contentLengthStr = findHeaderValues(headers, CONTENT_LENGTH);
        if (contentLengthStr == null) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_CONTENT_LENGTH_VALUE.getExceptionMessage());
        }
        return Integer.parseInt(contentLengthStr);
    }

    public static String getCookie(Map<String, String> headers) {
        return findHeaderValues(headers, COOKIE);
    }

    public static String writeHeaderLine(RequestHttpHeader headerName, String headerValue) {
        return headerName.getHeaderMessage() + ": " + headerValue + "\r\n";
    }
}
