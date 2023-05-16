package com.samsung.samsungproject.data.repository;

import com.samsung.samsungproject.data.api.user.UserApiService;
import com.samsung.samsungproject.data.dto.UserDto;
import com.samsung.samsungproject.domain.model.User;

import retrofit2.Call;

public class UserRepository {
    public static Call<UserDto> saveUser(UserDto userDto){
        return UserApiService.getInstance().saveUser(userDto);
    }
    public static Call<UserDto> getUserByEmail(String email){
        return  UserApiService.getInstance().getUserByEmail(email);
    }
    public static Call<UserDto> getUserById(long id){
        return  UserApiService.getInstance().getUserById(id);
    }
}
