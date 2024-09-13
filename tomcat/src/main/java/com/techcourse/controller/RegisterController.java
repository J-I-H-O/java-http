package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.CookieManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends Controller {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String sessionId = CookieManager.getCookieValue(request.getHeaderValue("Cookie"), "JSESSIONID");
        if (sessionId == null) {
            response.setBodyWithStaticResource("/register.html");
            return;
        }
        User user = SessionManager.get(sessionId);
        if (user == null) {
            response.setBodyWithStaticResource("/register.html");
            return;
        }

        log.info(user.toString());
        response.setStatusCode("302 Found");
        response.addHeader("Location", "/index.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String bodyInputAccount = request.getBodyParameter("account");
        String bodyInputPassword = request.getBodyParameter("password");
        String bodyInputEmail = request.getBodyParameter("email");

        if (bodyInputAccount == null || bodyInputPassword == null || bodyInputEmail == null) {
            response.setStatusCode("400 Bad Request");
            response.setBodyWithStaticResource("/register.html");
            return;
        }

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(bodyInputAccount);
        if (optionalUser.isEmpty()) {
            User user = new User(bodyInputAccount, bodyInputPassword, bodyInputEmail);
            InMemoryUserRepository.save(user);
            log.info(user.toString());

            String sessionId = SessionManager.put(user);
            response.setStatusCode("302 Found");
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
            response.addHeader("Location", "/index.html");
            return;
        }
        response.setStatusCode("400 Bad Request");
        response.setBodyWithStaticResource("/register.html");
    }
}
