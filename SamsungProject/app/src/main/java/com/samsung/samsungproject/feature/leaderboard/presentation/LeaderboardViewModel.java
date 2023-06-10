package com.samsung.samsungproject.feature.leaderboard.presentation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.samsung.samsungproject.data.db.dao.user.UserDao;
import com.samsung.samsungproject.data.db.dao.user.UserDaoSqlite;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.data.repository.communication.RepositoryCallback;
import com.samsung.samsungproject.data.repository.communication.Result;
import com.samsung.samsungproject.domain.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardViewModel extends AndroidViewModel {
    private final MutableLiveData<List<User>> _leaderboard = new MutableLiveData<>();
    public LiveData<List<User>> leaderboard = _leaderboard;
    private final MutableLiveData<LeaderboardStatus> _status = new MutableLiveData<>();
    public LiveData<LeaderboardStatus> status = _status;
    public UserRepository repository;

    public LeaderboardViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }


    public void load(){
        _status.setValue(LeaderboardStatus.LOADING);
        repository.getLeaderboard(result -> {
            if (result instanceof Result.Success){
                _status.setValue(LeaderboardStatus.SUCCESS);
                _leaderboard.setValue(((Result.Success<List<User>>) result).data);
            } else {
                _status.setValue(LeaderboardStatus.FAILURE);
                _leaderboard.setValue(((Result.Error<List<User>>) result).data);
            }
        });
    }
}
