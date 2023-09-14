package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.controller.util.BodyExtractor;
import org.apache.coyote.http11.exception.MemberAlreadyExistsException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.service.LoginService;
import org.apache.coyote.http11.session.SessionManager;

public class SignUpController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String INDEX_PAGE = "/index.html";
    public static final String REGISTER_PAGE = "/register.html";

    private final LoginService loginService;

    public SignUpController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (SessionManager.loggedIn(httpRequest)) {
            httpResponse.responseFrom(ResponseEntity.redirect(INDEX_PAGE));
            return;
        }
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView(REGISTER_PAGE);
        httpResponse.responseFrom(responseEntity);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            if (SessionManager.loggedIn(httpRequest)) {
                httpResponse.responseFrom(ResponseEntity.redirect(INDEX_PAGE));
                return;
            }
            String loginSession = signUp(httpRequest);
            httpResponse.responseFrom(ResponseEntity.status(302)
                .location(INDEX_PAGE)
                .sessionCookie(loginSession)
                .build());
        } catch (MemberAlreadyExistsException e) {
            httpResponse.responseFrom(ResponseEntity.redirect(REGISTER_PAGE));
        }
    }

    private String signUp(HttpRequest httpRequest) {
        Map<String, String> bodyData = BodyExtractor.convertBody(httpRequest.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        String email = bodyData.get(EMAIL);
        return loginService.signUp(account, password, email);
    }
}
