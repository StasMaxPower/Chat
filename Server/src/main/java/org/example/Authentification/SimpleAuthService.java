package org.example.Authentification;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {
    private List<UserData> users = new ArrayList<>();

    public SimpleAuthService() {
        users.add(new UserData("qwe", "qwe", "Alex"));
        users.add(new UserData("asd", "asd", "Ivan"));
        users.add(new UserData("zxc", "zxc", "Anna"));
    }

    @Override
    public String getNickName(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickName;
            }
        }
        return null;
    }

    private class UserData {
        private String login;
        private String password;
        private String nickName;

        public UserData(String login, String password, String nickName) {
            this.login = login;
            this.password = password;
            this.nickName = nickName;
        }
    }


}
