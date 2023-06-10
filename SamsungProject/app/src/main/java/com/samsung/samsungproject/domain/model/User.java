package com.samsung.samsungproject.domain.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

import java.io.Serializable;


public class User implements Serializable {

    private final long id;

    private final String email;

    private final String nickname;

    private final String password;

    private final String role;
    private final long score;

    private final static String EMAIL_KEY = "EMAIL_KEY";
    private final static String PASSWORD_KEY = "PASSWORD_KEY";

    public User(long id, String email, String nickname, String password, String role, int score) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.score = score;
    }

    public static String getEmailFromPreferences(FragmentActivity activity){
        return activity.getPreferences(Context.MODE_PRIVATE).getString(EMAIL_KEY, null);
    }
    public static void saveUserToPreferences(FragmentActivity activity, String email, String password){
        SharedPreferences.Editor editor = activity.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(EMAIL_KEY, email);
        editor.putString(PASSWORD_KEY, password);
        editor.apply();
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

    public long getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", score=" + score +
                '}';
    }
}
