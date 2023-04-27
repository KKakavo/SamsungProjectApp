package com.samsung.samsungproject.domain.model;

public class User {
    private final String email;
    private final String nickname;
    private final String password;
    private final String role;

    public User(String email, String nickname, String password, String role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
