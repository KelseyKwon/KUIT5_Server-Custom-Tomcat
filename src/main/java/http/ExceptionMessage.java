package http;

public enum ExceptionMessage {
    INVALID_CONTENT_LENGTH_VALUE("Content-Length 값이 제공되지 않았습니다"),
    INVALID_METHOD_TYPE("지원되지 않는 method 타입입니다"),
    INVALID_REQUEST_START_LINE("잘못된 Request 요청 방식입니다"),
    INVALID_REQUESTED_URL("잘못된 url 요청입니다");

    private final String exceptionMessage;

    ExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
