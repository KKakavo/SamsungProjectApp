package com.samsung.samsungproject.data.repository;

import com.samsung.samsungproject.data.api.user.UserApiService;
import com.samsung.samsungproject.domain.model.User;

import retrofit2.Call;

public class UserRepository {
    public static Call<User> saveUser(User user){
        return UserApiService.getInstance().saveUser(user);
    }
    public static Call<User> getUserByEmail(String email){
        return  UserApiService.getInstance().getUserByEmail(email);
    }
}
