package controller;

import db.MemoryUserRepository;
import http.commons.HttpMethod;
import http.commons.HttpRequestPath;
import http.commons.UserQueryKey;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

public class SignupController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        // POST 방식
        if (httpRequest.getRequestMethod() == HttpMethod.POST) {
            Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());
            User user = UserQueryKey.toUser(queryParams);
            MemoryUserRepository.getInstance().addUser(user);
            httpResponse.redirect(HttpRequestPath.HOME.getStaticFilePath());
        }

        //GET 방식
        if (httpRequest.getRequestMethod() == HttpMethod.GET) {
            String[] parts = httpRequest.getRequestPath().split("\\?");
            String queryString = parts.length > 1 ? parts[1] : "";
            Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(queryString);
            User user = UserQueryKey.toUser(queryParams);
            MemoryUserRepository.getInstance().addUser(user);
            httpResponse.redirect(HttpRequestPath.HOME.getStaticFilePath());
            return;

        }
    }
}
