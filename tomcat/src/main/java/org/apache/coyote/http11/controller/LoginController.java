package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.controller.util.BodyExtractor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.service.LoginService;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String INDEX_PAGE = "/index.html";
    public static final String UNAUTHORIZED_PAGE = "/401.html";
    public static final String LOGIN_PAGE = "/login.html";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (SessionManager.loggedIn(httpRequest)) {
            httpResponse.responseFrom(ResponseEntity.redirect(INDEX_PAGE));
            return;
        }
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView(LOGIN_PAGE);
        httpResponse.responseFrom(responseEntity);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (SessionManager.loggedIn(httpRequest)) {
            httpResponse.responseFrom(ResponseEntity.redirect(INDEX_PAGE));
            return;
        }

        Optional<String> loginSession = login(httpRequest);
        if (loginSession.isPresent()) {
            httpResponse.responseFrom(ResponseEntity.status(302)
                .location(INDEX_PAGE)
                .sessionCookie(loginSession.get())
                .build());
            return;
        }

        httpResponse.responseFrom(ResponseEntity.redirect(UNAUTHORIZED_PAGE));
    }

    private Optional<String> login(HttpRequest httpRequest) {
        Map<String, String> bodyData = BodyExtractor.convertBody(httpRequest.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        if (account != null && password != null) {
            return loginService.login(account, password);
        }
        return Optional.empty();
    }
}
