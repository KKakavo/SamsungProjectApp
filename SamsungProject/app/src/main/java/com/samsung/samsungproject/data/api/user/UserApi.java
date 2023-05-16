package com.samsung.samsungproject.data.api.user;

import com.samsung.samsungproject.data.dto.UserDto;
import com.samsung.samsungproject.domain.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @POST("user")
    Call<UserDto> saveUser(@Body UserDto userDto);

    @GET("user")
    Call<UserDto> getUserByEmail(@Query("email") String email);

    @GET("user/{id}")
    Call<UserDto> getUserById(@Path("id") long id);
}
