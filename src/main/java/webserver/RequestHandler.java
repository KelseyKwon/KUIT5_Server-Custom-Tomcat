package webserver;

import db.MemoryUserRepository;
import http.commons.*;
import http.request.*;
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
            String filePath = httpRequest.getRequestPath();
            HttpMethod method = httpRequest.getRequestMethod();
            Map<String, String> headers = httpRequest.getHttpHeaders();
            String body = httpRequest.getBody();

            if ("/".equals(filePath)) {
                filePath = "/index.html";
            }

            //=== 요구사항 2. GET 방식으로 회원가입 구현=== "/user/signup" //
            // http://localhost/user/signup?userId=nykwon7777&password=1234&name=222&email=333%40fff
            if (filePath.startsWith(HttpRequestPath.SIGNUP.getUrl()) && method == HttpMethod.GET) {
                String[] parts = filePath.split("\\?");
//                String onlyPath = parts[0];
                String queryString = parts.length > 1 ? parts[1] : "";
                Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(queryString);
                User user = UserQueryKey.toUser(queryParams);
                MemoryUserRepository.getInstance().addUser(user);
                response302Header(dos, HttpRequestPath.HOME.getStaticFilePath());
                return;
            }

            //=== 요구사항 3. POST 방식으로 회원가입 구현===//
            // http://localhost/user/signup?userId=nykwon7777&password=1234&name=222&email=333%40fff
            if (filePath.startsWith(HttpRequestPath.SIGNUP.getUrl()) && method == HttpMethod.POST) {
                Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(body);
                User user = UserQueryKey.toUser(queryParams);
                MemoryUserRepository.getInstance().addUser(user);
                response302Header(dos, HttpRequestPath.HOME.getStaticFilePath());
                return;
            }

            //=====요구사항 5. login 기능=====//
            if (filePath.startsWith(HttpRequestPath.LOGIN.getUrl()) && method == HttpMethod.POST) {
                int contentLength = RequestHttpHeader.getContentLength(headers);
                Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(body);


                String userId = queryParams.get(UserQueryKey.USERID.getQueryKey());
                String password = queryParams.get(UserQueryKey.PASSWORD.getQueryKey());

                // 입력값 검증
                if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
                    response302Header(dos, HttpRequestPath.LOGIN_FAILED.getUrl());
                    return;
                }

                // DB에서 id로 찾은 userId가 쿼리로 얻은 userId와 같은지.
                MemoryUserRepository userRepository = MemoryUserRepository.getInstance();
                User userById = userRepository.findUserById(userId);

                if (userById.getUserId().equals(userId) && userById.getPassword().equals(password)) {
                    response302Header(dos, HttpRequestPath.HOME.getStaticFilePath(), "logined-true");
                    return;
                } else {
                    response302Header(dos, HttpRequestPath.LOGIN_FAILED.getUrl());
                    return;
                }

            }

            //=====요구사항 6. 사용자 목록 출력=====//
            if (filePath.startsWith(HttpRequestPath.USER_LIST.getUrl())) {
                String cookieStr = RequestHttpHeader.getCookie(headers);
                if ("logined-true".equals(cookieStr)) {
                    Path listPath = Paths.get("webapp", HttpRequestPath.USER_LIST.getStaticFilePath());
                    byte[] pathBody = Files.readAllBytes(listPath);
                    response200Header(dos, pathBody.length);
                    responseBody(dos, pathBody);
                } else {
                    response302Header(dos, HttpRequestPath.LOGIN.getStaticFilePath());
                }
                return;
            }

            //=====요구사항1 & 7. 정적 파일 처리====//
            Path path = Paths.get("webapp" + filePath);

            if (Files.exists(path)) {
                byte[] pathBody = Files.readAllBytes(path);
                String contentType = getContentType(filePath);
                response200Header(dos, pathBody.length, contentType);
                responseBody(dos, pathBody);
            } else {
                byte[] pathBody = "404 Not Found".getBytes();
                response200Header(dos, pathBody.length, "text/plain");
                responseBody(dos, pathBody);
            }


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".css")) return "text/css";
        return "text/html;charset=utf-8";
    }

    /**
     * 응답 헤더 생성
     */
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        response200Header(dos, lengthOfBodyContent, "text/html;charset=utf-8");
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String typeOfBodyContent) {
        try {
            dos.writeBytes(HttpStatusCode.writeStatusLine(HttpStatusCode.SUCCESS));
            dos.writeBytes(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.CONTENT_TYPE, typeOfBodyContent));
            dos.writeBytes(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.CONTENT_LENGTH, String.valueOf(lengthOfBodyContent)));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * 응답 본문 전송 후 flush로 스트림 비우기
     */
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 302 : 일시 리다이렉션
    private void response302Header(DataOutputStream dos, String path) {
        response302Header(dos, path, null);
    }

    private void response302Header(DataOutputStream dos, String path, String cookie) {
        try {
            dos.writeBytes(HttpStatusCode.writeStatusLine(HttpStatusCode.REDIRECT));
            dos.writeBytes(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.LOCATION, path));
            if (cookie != null) {
                dos.writeBytes(RequestHttpHeader.writeHeaderLine(RequestHttpHeader.SET_COOKIE, cookie));
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
