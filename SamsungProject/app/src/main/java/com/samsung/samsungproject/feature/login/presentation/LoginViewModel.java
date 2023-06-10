package com.samsung.samsungproject.feature.login.presentation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.data.repository.communication.RepositoryCallback;
import com.samsung.samsungproject.data.repository.communication.Result;
import com.samsung.samsungproject.domain.model.User;

import java.io.IOException;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginViewModel extends AndroidViewModel {
    private UserRepository repository;
    private MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;
    private MutableLiveData<LoginStatus> _status = new MutableLiveData<>();
    public LiveData<LoginStatus> status = _status;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }
    public void login(String email, String password){
        _status.setValue(LoginStatus.LOADING);
        repository.getUserByEmail(email, new RepositoryCallback<Result>() {
            @Override
            public void onComplete(Result result) {
                if(result instanceof Result.Success){
                    User user = ((Result.Success<User>) result).data;
                    BCrypt.Result bcrypt = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray());
                    if(bcrypt.verified) {
                        _user.setValue(user);
                        _status.setValue(LoginStatus.SUCCESS);
                    } else {
                        _status.setValue(LoginStatus.WRONG_PASSWORD);
                    }
                } else {
                    Exception exception = (((Result.Error<?>) result).exception);
                    if(exception instanceof IOException)
                        _status.setValue(LoginStatus.SERVER_NOT_RESPONDING);
                    else
                        _status.setValue(LoginStatus.WRONG_EMAIL);
                }
            }
        });
    }
}
