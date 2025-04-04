package http.commons;

public enum HttpStatusCode {
    SUCCESS(200, "OK"),
    REDIRECT(302, "Redirect"),
    FAILED(404, "Not Found");

    private final int code;
    private final String description;

    HttpStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static byte[] writeStatusLine(HttpStatusCode statusCode) {
        return ("HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getDescription() + " \r\n").getBytes();
    }

}
