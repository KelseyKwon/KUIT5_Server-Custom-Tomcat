package http.response;

import http.commons.HttpStatusCode;
import http.commons.RequestHttpHeader;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HttpResponse도 HttpRequest와 비슷하게 OutputStream에 쓰는 Response 메시지를 쓰는 책임을 분리할 수 있을 것 같다.
 *
 * outputStream을 인자로 받아 원하는 html 파일을 보여주는 forward(path) 함수와 redirect 시켜주는 redirect(path) 함수를 구현해보자.
 */
public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());

    private static OutputStream dos;
    public HttpResponse(OutputStream outputStream) {
        this.dos = outputStream;
    }

    // path를 분기문으로 나눠서 200, 302 상태의 메소드를 담당하는 곳으로 넘기기
    // 정적 html 파일을 보여주기
    public void forward(String filePath) throws IOException {

        if ("/".equals(filePath) || "".equals(filePath)) {
            filePath = "index.html";
        }

        Path path = Paths.get("webapp", filePath);
        if (Files.exists(path)) {
            byte[] pathBody = Files.readAllBytes(path);
            String contentType = getContentType(filePath);
            response200Header(pathBody.length, contentType);
            responseBody(pathBody);
        } else {
            staticFileFromPathDoesNotExist(filePath);
        }

    }

    // redirect 시켜주기
    public void redirect(String filePath) {
        Path path = Paths.get("webapp", filePath);
        if (Files.exists(path)) {
            response302Header(filePath, null);
        } else {
            staticFileFromPathDoesNotExist(filePath);
        }
    }

    public void redirect(String filePath, String cookie) {
        Path path = Paths.get("webapp", filePath);
        if (Files.exists(path)) {
            response302Header(filePath, cookie);
        } else {
            staticFileFromPathDoesNotExist(filePath);
        }
    }


    public void response200Header(int bodyLength, String bodyType) {
                try {
            dos.write(HttpStatusCode.writeStatusLine(HttpStatusCode.SUCCESS));
            dos.write(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.CONTENT_TYPE, bodyType));
            dos.write(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.CONTENT_LENGTH, String.valueOf(bodyLength)));
            dos.write("\r\n".getBytes());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302Header(String path, String cookie) {
        try {
            dos.write(HttpStatusCode.writeStatusLine(HttpStatusCode.REDIRECT));
            dos.write(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.LOCATION, path));
            if (cookie != null) {
                dos.write(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.SET_COOKIE, cookie));
            }
            dos.write("\r\n".getBytes());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(int bodyLength, String bodyType) {
        try {
            dos.write(HttpStatusCode.writeStatusLine(HttpStatusCode.FAILED));
            dos.write(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.CONTENT_TYPE, bodyType));
            dos.write(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.CONTENT_LENGTH, String.valueOf(bodyLength)));
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void staticFileFromPathDoesNotExist(String filePath) {
        byte[] pathBody = "404 Not Found".getBytes();
        String contentType = getContentType(filePath);
        response404Header(pathBody.length, contentType);
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".css")) return "text/css";
        return "text/html;charset=utf-8";
    }
}
