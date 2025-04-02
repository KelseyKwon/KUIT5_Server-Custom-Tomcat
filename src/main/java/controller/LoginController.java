package controller;

import db.MemoryUserRepository;
import http.commons.HttpRequestPath;
import http.commons.RequestHttpHeader;
import http.commons.UserQueryKey;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

public class LoginController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        int contentLength = RequestHttpHeader.getContentLength(httpRequest.getHttpHeaders());
        Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());

        String userId = queryParams.get(UserQueryKey.USERID.getQueryKey());
        String password = queryParams.get(UserQueryKey.PASSWORD.getQueryKey());

        // 입력값 검증
        if (isUserIdAndPasswordNull(userId, password)) {
            httpResponse.redirect(HttpRequestPath.LOGIN_FAILED.getUrl());
            return;
        }

        // DB에서 id로 찾은 userId가 쿼리로 얻은 userId와 같은지.
        User userById = MemoryUserRepository.getInstance().findUserById(userId);

        if (validUserId(userById, userId) && validUserPassword(userById, password)) {
            httpResponse.redirect(HttpRequestPath.HOME.getStaticFilePath(), "logined-true");
            return;
        } else {
            httpResponse.redirect(HttpRequestPath.LOGIN_FAILED.getUrl());
            return;
        }
    }
    private static boolean isUserIdAndPasswordNull(String userId, String password) {
        return userId == null || userId.isBlank() || password == null || password.isBlank();
    }

    private static boolean validUserId(User userById, String userId) {
        return userById.getUserId().equals(userId);
    }

    private static boolean validUserPassword(User userById, String password) {
        return userById.getPassword().equals(password);
    }
}
