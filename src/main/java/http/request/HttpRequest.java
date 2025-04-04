package http.request;

import http.commons.HttpMethod;
import http.commons.HttpRequestPath;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static http.ExceptionMessage.INVALID_REQUEST_START_LINE;

public class HttpRequest {
    private final HttpMethod requestMethod;
    private final String requestPath;
    private final String httpVersion;
    private final Map<String, String> httpHeaders;
    private final String body;

    public HttpRequest(HttpMethod requestMethod, String requestPath, String httpVersion, Map<String, String> httpHeaders, String body) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.httpVersion = httpVersion;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        List<String> requestStartLine = getRequestStartLineInstance(br);
        Map<String, String> requestHeaders = getRequestHeadersInstance(br);
        String body = getRequestBodyInstance(br, requestHeaders);

        return new HttpRequest(HttpMethod.from(requestStartLine.get(0)), requestStartLine.get(1), requestStartLine.get(2), requestHeaders, body);
    }

    private static List<String> getRequestStartLineInstance(BufferedReader br) throws IOException {
        String requestStartLine = br.readLine();
        validateRequestStartLine(isRequestStartLineANull(requestStartLine));

        List<String> requests = Arrays.stream(requestStartLine.split(" ")).toList();
        validateRequestStartLine(isRequestStartLineHasLackOfSize(requests));

        return requests;
    }

    private static Map<String, String> getRequestHeadersInstance(BufferedReader br) throws IOException {
        String line = br.readLine();
        StringBuilder sb = new StringBuilder();
        while (isRequestHeaderLineEnded(line)) {
            sb.append(line).append("\r\n");
            line = br.readLine();
        }
        return Arrays.stream(sb.toString().split("\r\n"))
                .map(header -> header.split(": ", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    private static String getRequestBodyInstance(BufferedReader br, Map<String, String> requestHeaders) throws IOException {
        int bodyLength = requestHeaders.containsKey("Content-Length") ? Integer.parseInt(requestHeaders.get("Content-Length")) : 0;
        return IOUtils.readData(br, bodyLength);
    }

    private static void validateRequestStartLine(boolean requestStartLine) {
        if (requestStartLine) {
            throw new IllegalArgumentException(INVALID_REQUEST_START_LINE.getExceptionMessage());
        }
    }

    private static boolean isRequestStartLineHasLackOfSize(List<String> requests) {
        return requests.size() != 3;
    }

    private static boolean isRequestStartLineANull(String requestStartLine) {
        return requestStartLine == null;
    }


    private static boolean isRequestHeaderLineEnded(String line) {
        return !"".equals(line);
    }

    private static boolean bodyHasCharacters(int bodyLength) {
        return bodyLength > 0;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public String getBody() {
        return body;
    }
}
