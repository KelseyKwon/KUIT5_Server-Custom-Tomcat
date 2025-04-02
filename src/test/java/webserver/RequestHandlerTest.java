package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {
    @BeforeEach
    void setUp() {
        MemoryUserRepository.getInstance().clear();
    }

    @Test
    @DisplayName("회원가입 GET 방식 성공 테스트")
    void signUpByGETMethod() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /user/signup?userId=nykwon7777&password=1234&name=Kwon&email=nykwon7777@gmail.com HTTP/1.1",
                "Host: localhost:80",
                "", // 헤더 없어
                ""  // 바디 없어
        );

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String response = socket.output();

        assertThat(response).contains("HTTP/1.1 302 Redirect");
        assertThat(response).contains("Location: /index.html");


        // 유저 저장 확인
        User saved = MemoryUserRepository.getInstance().findUserById("nykwon7777");
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Kwon");
    }

    @Test
    @DisplayName("회원가입 POST 방식 성공 테스트")
    void signUpByPOSTMethod()  {
        //given
        String body = "userId=nykwon7777&password=1234&name=Kwon&email=nykwon7777@gmail.com";
        String httpRequest = String.join("\r\n",
                "POST /user/signup HTTP/1.1",
                "Host: localhost",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "", // 헤더 없어
                body // 실제 body
        );

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandler requestHandler = new RequestHandler(socket);

        //when
        requestHandler.run();

        //then
        String response = socket.output();

        assertThat(response).contains("HTTP/1.1 302 Redirect");
        assertThat(response).contains("Location: /index.html");

        // 유저 저장 확인
        User saved = MemoryUserRepository.getInstance().findUserById("nykwon7777");
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Kwon");

    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccessTest() {
        // given: 테스트 전에 올바른 유저를 등록
        MemoryUserRepository.getInstance().addUser(
                new User("nykwon7777", "1234", "Kwon", "nykwon7777@gmail.com")
        );

        String body = "userId=nykwon7777&password=1234";
        String httpRequest = String.join("\r\n",
                "POST /user/login HTTP/1.1",
                "Host: localhost",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes().length,
                "",  // 헤더 없어
                body   // 실제 body
        );

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then: 302 Redirect, 쿠키에 logined-true, 그리고 Location 헤더가 /index.html로 설정되어야 함
        String response = socket.output();
        assertThat(response).contains("HTTP/1.1 302 Redirect");
        assertThat(response).contains("Set-Cookie: logined-true");
        assertThat(response).contains("Location: /index.html");
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void loginFailureTest() {
        // given: 테스트 전에 올바른 유저를 등록
        MemoryUserRepository.getInstance().addUser(
                new User("nykwon7777", "1234", "Kwon", "nykwon7777@gmail.com")
        );

        // 비밀번호를 틀리게 전송
        String body = "userId=nykwon7777&password=0000";
        String httpRequest = String.join("\r\n",
                "POST /user/login HTTP/1.1",
                "Host: localhost",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes().length,
                "", // 헤더 끝
                body   // 실제 body
        );

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then: 302 Redirect가 발생하며, Location 헤더에 로그인 실패 페이지인 /user/login_failed.html이 설정되어야 함
        String response = socket.output();
        assertThat(response).contains("HTTP/1.1 302 Redirect");
        assertThat(response).contains("Location: /user/login_failed.html");
    }

    @Test
    @DisplayName("유저 리스트 로딩 테스트 (로그인 상태)")
    void userListLoadingTest() {
        // given: 유저 리스트 요청 시, 쿠키에 "logined-true"가 포함된 경우
        String httpRequest = String.join("\r\n",
                "GET /user/userList HTTP/1.1",
                "Host: localhost",
                "Cookie: logined-true",
                "", // 헤더 끝
                ""  // GET은 body 없음
        );

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then: 유저 리스트를 반환하는 페이지(여기서는 /index.html 파일 내용)를 200 OK로 응답해야 함
        String response = socket.output();
        assertThat(response).contains("HTTP/1.1 200 OK");
        assertThat(response).contains("Content-Type: text/html");
    }

    @Test
    @DisplayName("유저 리스트 로딩 테스트 (로그인 상태가 아님)")
    void userListLoadingFailTest() {
        // given: 유저 리스트 요청 시, 쿠키에 "logined-true"가 포함되지 않은 경우
        String httpRequest = String.join("\r\n",
                "GET /user/userList HTTP/1.1",
                "Host: localhost",
                "", // 헤더 끝
                ""  // GET은 body 없음
        );

        StubSocket socket = new StubSocket(httpRequest);
        RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then: 유저 리스트를 반환하는 페이지(여기서는 /index.html 파일 내용)를 200 OK로 응답해야 함
        String response = socket.output();
        assertThat(response).contains("HTTP/1.1 302 Redirect");
        assertThat(response).contains("Location: /user/login.html");
    }

}