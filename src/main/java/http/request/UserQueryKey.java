package http.request;

import model.User;

import java.util.Map;

public enum UserQueryKey {
    USERID("userId"),
    PASSWORD("password"),
    USERNAME("name"),
    EMAIL("email");

    private final String queryKey;

    UserQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public String getQueryKey() {
        return queryKey;
    }

    public static User toUser(Map<String, String> queryParamMap) {
        String userId = queryParamMap.get(USERID.getQueryKey());
        String password = queryParamMap.get(PASSWORD.getQueryKey());
        String name = queryParamMap.get(USERNAME.getQueryKey());
        String email = queryParamMap.get(EMAIL.getQueryKey());

        return new User(userId, password, name, email);
    }
}
