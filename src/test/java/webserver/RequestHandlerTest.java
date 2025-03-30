package webserver;

import db.MemoryUserRepository;
import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;
import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {
    @Test
    @DisplayName("회원가입 GET 방식 성공 테스트")
    void signUpByGETMethod() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /user/signup?userId=nykwon7777&password=1234&name=Kwon&email=nykwon7777@gmail.com HTTP/1.1",
                "Host: localhost:80",
                "", // 헤더 끝
                ""  // GET은 바디 없음
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
                "Content-Length: " + body.getBytes().length,
                "", // 헤더 끝
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

}