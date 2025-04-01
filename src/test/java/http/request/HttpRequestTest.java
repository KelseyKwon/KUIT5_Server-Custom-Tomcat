package http.request;

import http.commons.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {
    private final String testDirectory = "src/test/resources/";
    private final String getPath = "httprequestmessage.txt";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    @DisplayName("HttpRequest class 작동 테스트")
    void httpRequestClassTest() throws Exception {
        //given
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + getPath));

        //then
        assertEquals(HttpMethod.POST, httpRequest.getRequestMethod());
        assertEquals("/user/create", httpRequest.getRequestPath());
        assertEquals("HTTP/1.1", httpRequest.getHttpVersion());
        assertEquals("localhost:8080", httpRequest.getHttpHeaders().get("Host"));
        assertEquals("keep-alive", httpRequest.getHttpHeaders().get("Connection"));
        assertEquals("40", httpRequest.getHttpHeaders().get("Content-Length"));
        assertEquals("*/*", httpRequest.getHttpHeaders().get("Accept"));
        assertEquals("userId=jw&password=password&name=jungwoo", httpRequest.getBody());
    }

}