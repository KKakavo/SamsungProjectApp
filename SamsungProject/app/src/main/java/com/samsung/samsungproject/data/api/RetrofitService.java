package com.samsung.samsungproject.data.api;

import com.samsung.samsungproject.domain.model.User;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitService {
    private static final String BASE_URL = "http://195.2.67.53:8080";
    private static Retrofit retrofit;
    private static Retrofit create(){
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static Retrofit getInstance(){
        if(retrofit == null) retrofit = create();
        return retrofit;
    }
    public static void addCredentials(String user, String password){
        /*OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new BasicAuthInterceptor(user, password)).build();
        retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build();*/
    }
}
