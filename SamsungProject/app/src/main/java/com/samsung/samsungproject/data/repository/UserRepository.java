package com.samsung.samsungproject.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.samsung.samsungproject.data.api.user.UserApi;
import com.samsung.samsungproject.data.api.user.UserApiService;
import com.samsung.samsungproject.data.db.dao.user.UserDao;
import com.samsung.samsungproject.data.db.dao.user.UserDaoSqlite;
import com.samsung.samsungproject.data.repository.communication.RepositoryCallback;
import com.samsung.samsungproject.data.repository.communication.Result;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.exception.UserAlreadyExistsException;
import com.samsung.samsungproject.exception.UserNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class UserRepository {
    private final static String EMAIL_KEY = "EMAIL_KEY";
    private final static String PASSWORD_KEY = "PASSWORD_KEY";
    private final String LOG_TAG = "UserRepository";
    private final UserApi api;
    private final UserDao dao;
    private final Executor executor;
    private final Handler handler;

    public UserRepository(Application application){
        api = UserApiService.getInstance();
        dao = UserDaoSqlite.getInstance(application.getApplicationContext());
        executor = Executors.newSingleThreadExecutor();
        handler = new android.os.Handler(Looper.getMainLooper());
    }
    public void saveUser(User user,
                         RepositoryCallback<Result> callback){
        executor.execute(() -> {
            try {
                Response<User> response = api.saveUser(user).execute();
                if(response.isSuccessful()){
                    Result result = new Result.Success<>(response.body());
                    handler.post(() -> callback.onComplete(result));
                } else if(response.code() == 400) {
                    throw new UserAlreadyExistsException("User with email: " + user.getEmail() + " already exists");
                } else throw new IOException("http code: " + response.code() + " in method saveUser");
            } catch (IOException | UserAlreadyExistsException e) {
                Log.e(LOG_TAG, e.getMessage());
                Result result = new Result.Error<>(e, null);
                handler.post(() -> callback.onComplete(result));
            }
        });
    }
    public void getUserByEmail(String email,
                               RepositoryCallback<Result> callback){
        executor.execute(() -> {
            try {
                Response<User> response = api.getUserByEmail(email).execute();
                if(response.isSuccessful()){
                    Result result = new Result.Success<>(response.body());
                    handler.post(() -> callback.onComplete(result));
                } else if ( response.code() == 400)
                    throw new UserNotFoundException("User with email: " + email + " not found");
                else throw new IOException("http code: " + response.code() + " in method getUserByEmail");
            } catch (IOException | UserNotFoundException e) {
                Log.e(LOG_TAG, e.getMessage());
                Result result = new Result.Error<>(e, null);
                handler.post(() -> callback.onComplete(result));
            }
        });
    }
    public void getLeaderboard(RepositoryCallback<Result> callback){
        executor.execute(() -> {
            try {
                Response<List<User>> response = api.getLeaderboard().execute();
                if(response.isSuccessful()){
                    List<User> body = response.body();
                    dao.deleteAll();
                    dao.insertAll(body);
                    Result result = new Result.Success<>(body);
                    handler.post(() -> callback.onComplete(result));
                } else throw new IOException("http code: " + response.code() + " in method getLeaderboard");
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                List<User> leaderboard = dao.findAll();
                Result result = new Result.Error<>(e, leaderboard);
                handler.post(() -> callback.onComplete(result));
            }
        });
    }

}
