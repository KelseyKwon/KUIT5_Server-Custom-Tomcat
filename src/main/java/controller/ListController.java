package controller;

import http.commons.HttpRequestPath;
import http.commons.RequestHttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String cookieStr = RequestHttpHeader.getCookie(httpRequest.getHttpHeaders());
        if ("logined-true".equals(cookieStr)) {
            httpResponse.forward(HttpRequestPath.USER_LIST.getStaticFilePath());
        } else {
            httpResponse.redirect(HttpRequestPath.LOGIN.getStaticFilePath());
        }
        return;
    }
}
