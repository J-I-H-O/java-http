package org.apache.catalina;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, User> sessions = new HashMap<>();

    public static User get(String sessionId) {
        User user = sessions.get(sessionId);
        if (user == null) {
            throw new IllegalArgumentException("No such user exists.");
        }
        return user;
    }

    // TODO: sessionId 리턴을 명시적으로 드러내도록 메서드 네이밍 수정
    public static String put(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }
}
