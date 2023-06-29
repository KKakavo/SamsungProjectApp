package com.samsung.samsungproject.data.api.user;

import com.samsung.samsungproject.domain.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @POST("user")
    Call<User> saveUser(@Body User user);

    @GET("user")
    Call<User> getUserByEmail(@Query("email") String email);
    @GET("user/{id}")
    Call<User> getUserById(@Path("id") long id);

    @GET("user/leaderboard")
    Call<List<User>> getLeaderboard();
    @PATCH("user/{id}/score")
    Call<Void> updateUserScoreById(@Path("id") long id, @Query("score") long score);
}
