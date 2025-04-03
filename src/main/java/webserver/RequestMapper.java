package webserver;

import controller.*;
import http.commons.HttpMethod;
import http.commons.HttpRequestPath;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private Controller controller;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public void proceed() throws IOException {
        Map<String, Controller> pathControllerMap = urlToControllerMapper();
        // 찾는 키가 존재하면 찾는 키의 값을 반환하고 없으면 기본 값을 반환 (2번째 attribute)
        controller = pathControllerMap.getOrDefault(httpRequest.getRequestPath(), new ForwardController());

        // ForwardController인 경우에는 forward() 메서드를 호출하도록 처리
        if (controller instanceof ForwardController) {
            httpResponse.forward(httpRequest.getRequestPath());
        } else {
            controller.execute(httpRequest, httpResponse);
        }
    }

    private static Map<String, Controller> urlToControllerMapper() {
        Map<String, Controller> controllers = new HashMap<>();

        controllers.put("/user/signup", new SignupController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new ListController());
        controllers.put("/index.html", new ForwardController());

        return controllers;
    }
}
