package com.samsung.samsungproject.data.dto;

import com.samsung.samsungproject.domain.model.User;

public class UserDto {
    private long id;
    private String email;
    private String nickname;
    private String password;
    private int score;
    private String role;
    public static UserDto toDto(User user){

        return new UserDto(user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPassword(),
                user.getScore(),
                user.getRole());

    }

    public static User toDomainObject(UserDto userDto){

        return new User(userDto.getId(),
                userDto.getEmail(),
                userDto.getNickname(),
                userDto.getPassword(),
                userDto.getRole(),
                userDto.getScore());

    }

    public UserDto(long id, String email, String nickname, String password, int score, String role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.score = score;
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

    public void setScore(int score) {
        this.score = score;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
