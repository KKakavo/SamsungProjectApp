package com.samsung.samsungproject.data.repository;

import com.samsung.samsungproject.data.api.shape.ShapeApiService;
import com.samsung.samsungproject.data.api.user.UserApiService;
import com.samsung.samsungproject.domain.model.User;

import java.util.List;

import retrofit2.Call;

public class UserRepository {
    public static Call<User> saveUser(User user){
        return UserApiService.getInstance().saveUser(user);
    }
    public static Call<User> getUserByEmail(String email){
        return  UserApiService.getInstance().getUserByEmail(email);
    }
    public static Call<User> getUserById(long id){
        return UserApiService.getInstance().getUserById(id);
    }
    public static Call<List<User>> getLeaderBoard(){
        return UserApiService.getInstance().getLeaderBoard();
    }
    public static Call<Void> updateUserScoreById(long id, long score){
        return UserApiService.getInstance().updateUserScoreById(id, score);
    }
}
