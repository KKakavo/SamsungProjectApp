package com.samsung.samsungproject.domain.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "users")
public class User implements Serializable {

    @PrimaryKey
    private long id;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "nickname")
    private String nickname;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "role")
    private String role;
    @ColumnInfo(name = "score")
    private int score;
    @Ignore
    private final static String ID_KEY = "ID_KEY";
    @Ignore
    private final static String PASSWORD_KEY = "PASSWORD_KEY";


    public User(long id, String email, String nickname, String password, String role, int score) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.score = score;
    }

    @Ignore
    public User(String email, String nickname, String password, String role, int score) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.score = score;
    }

    public static long getIdFromSharedPreferences(FragmentActivity activity){
        return activity.getPreferences(Context.MODE_PRIVATE).getLong(ID_KEY, 0);
    }

    public static String getPasswordFromSharedPreferences(FragmentActivity activity){
        return activity.getPreferences(Context.MODE_PRIVATE).getString(PASSWORD_KEY, null);
    }
    public static void insertUserIntoSharedPreferences(FragmentActivity activity, long id, String password){
        SharedPreferences.Editor editor = activity.getPreferences(Context.MODE_PRIVATE).edit();
        editor.putLong(ID_KEY, id);
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

    public int getScore() {
        return score;
    }

    public String getRole() {
        return role;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
