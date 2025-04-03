package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import http.commons.*;
import http.request.*;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static http.ExceptionMessage.*;

// Runnable을 통해 별도 스레드에서 "Hello World"라는 간단한 Http 응답을 전송하는 간이 HTTP 서버 핸들러.
public class RequestHandler implements Runnable{
    Socket connection; // 클라이언트와의 통신을 담당하는 소켓
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    /**
     * 1. 연결 정보 출력
     * 2. 입출력 스트림 열기 (br : client의 문자열 요청을 읽기 위한 래퍼, dos : 응답을 바이트로 보내기 위한 스트림)
     * 3. 클라이언트에 HTTP 응답 전송. (responseBody로 본문, response200Header로 헤더 전송)
     */
    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
