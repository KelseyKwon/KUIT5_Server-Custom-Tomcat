package http.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;



class HttpResponseTest {

    private final String testDirectory = "src/test/resources/";
    private final String forwardPath = "httprequestmessage.txt";

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    @DisplayName("HttpResponse class 작동 테스트")
    void httpResponseClassTest() throws Exception {
        //given
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+forwardPath));

        //then
        httpResponse.forward("/index.html");
    }

}