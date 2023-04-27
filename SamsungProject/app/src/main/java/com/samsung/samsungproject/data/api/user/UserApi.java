package com.samsung.samsungproject.data.api.user;

import com.samsung.samsungproject.domain.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApi {

    @POST("user")
    Call<User> saveUser(@Body User user);

    @GET("user")
    Call<User> getUserByEmail(@Query("email") String email);
}
