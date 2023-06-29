package com.samsung.samsungproject.feature.registration.presentation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.data.repository.communication.Result;
import com.samsung.samsungproject.domain.model.User;

import java.io.IOException;

public class RegistrationViewModel extends AndroidViewModel {

    private final UserRepository repository;
    public MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;
    public MutableLiveData<RegistrationStatus> _status = new MutableLiveData<>();
    public LiveData<RegistrationStatus> status = _status;
    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }
    public void saveUser(User user){
        _status.setValue(RegistrationStatus.LOADING);
        repository.saveUser(user, result -> {
            if(result instanceof Result.Success){
                _status.setValue(RegistrationStatus.SUCCESS);
                _user.setValue(((Result.Success<User>) result).data);
            } else {
                Exception exception = ((Result.Error<?>) result).exception;
                if (exception instanceof IOException)
                    _status.setValue(RegistrationStatus.SERVER_NOT_RESPONDING);
                else
                    _status.setValue(RegistrationStatus.WRONG_CREDENTIALS);
            }
        });
    }
}
