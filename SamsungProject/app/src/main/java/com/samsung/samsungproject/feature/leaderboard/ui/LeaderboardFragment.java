package com.samsung.samsungproject.feature.leaderboard.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentLeaderboardBinding;
import com.samsung.samsungproject.domain.db.dao.user.UserDao;
import com.samsung.samsungproject.domain.db.dao.user.UserDaoSqlite;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.leaderboard.ui.recycler.UserAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private UserAdapter adapter;
    UserDao userDao;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDao = new UserDaoSqlite(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater);
        binding.btBack.setOnClickListener(v -> Navigation.findNavController(
                binding.getRoot()).navigate(com.samsung.samsungproject.feature.leaderboard.ui.LeaderboardFragmentDirections.actionLeaderboardFragmentToMapFragment(null)));
        adapter = new UserAdapter();
        binding.recycler.setAdapter(adapter);
        binding.btRefresh.setOnClickListener(v -> downloadLeaders());
        downloadLeaders();
        bindLeaderBoard();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void bindLeaderBoard(){
        List<User> leaderboard = new ArrayList<>(userDao.findAll());
        if(leaderboard.size() != 0) {
            binding.tvNickname.setText("@" + leaderboard.get(0).getNickname());
            binding.tvPoints.setText(leaderboard.get(0).getScore() + " м2");
            leaderboard.remove(0);
            adapter.setUserList(leaderboard);
        }
    }
    private void downloadLeaders() {
        UserRepository.getLeaderBoard().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    userDao.deleteAll();
                    response.body().forEach(userDao::insert);
                    bindLeaderBoard();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }
}