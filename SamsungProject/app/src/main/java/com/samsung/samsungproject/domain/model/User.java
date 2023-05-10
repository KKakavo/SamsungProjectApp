package com.samsung.samsungproject.domain.model;

import java.util.List;

public class User {

    private long id;
    private String email;
    private String nickname;
    private String password;
    private String role;

    public User(long id, String email, String nickname, String password, String role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public User(String email, String nickname, String password, String role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public long getId() {
        return id;
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
