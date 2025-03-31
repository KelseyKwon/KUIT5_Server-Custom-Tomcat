package http.request;

public enum HttpStatusCode {
    SUCCESS(200, "OK"),
    REDIRECT(302, "Redirect");
//    UNKNOWN(-1,"Unknown Status");


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

    public static String writeStatusLine(HttpStatusCode statusCode) {
        return "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getDescription() + " \r\n";
    }

//    public static StatusCode getStatusFromCode(int code) {
//        for (StatusCode status : StatusCode.values()) {
//            if (status.getCode() == code) {
//                return status;
//            }
//        }
//        return UNKNOWN;
//    }
}
