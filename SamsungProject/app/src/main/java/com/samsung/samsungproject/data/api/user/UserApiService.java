package com.samsung.samsungproject.data.api.user;

import com.samsung.samsungproject.data.api.RetrofitService;

public class UserApiService {
    private static UserApi userApi;
    private static UserApi create(){
        return RetrofitService.getInstance().create(UserApi.class);
    }

    public static UserApi getInstance(){
        if(userApi == null) userApi = create();
        return userApi;
    }
}
