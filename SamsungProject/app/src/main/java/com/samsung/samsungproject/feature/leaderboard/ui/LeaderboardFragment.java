package com.samsung.samsungproject.feature.leaderboard.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentLeaderboardBinding;
import com.samsung.samsungproject.domain.db.dao.user.UserDao;
import com.samsung.samsungproject.domain.db.dao.user.UserDaoSqlite;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.leaderboard.presentation.LeaderboardViewModel;
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
    private LeaderboardViewModel viewModel;
    private User authorizedUser;
    private LeaderboardFragmentArgs args;
    UserDao userDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDao = new UserDaoSqlite(requireContext());
        args = LeaderboardFragmentArgs.fromBundle(requireArguments());
        authorizedUser = args.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
        binding = FragmentLeaderboardBinding.inflate(inflater);
        binding.btBack.setOnClickListener(v -> Navigation.findNavController(
                binding.getRoot()).navigate(com.samsung.samsungproject.feature.leaderboard.ui.LeaderboardFragmentDirections.actionLeaderboardFragmentToMapFragment(authorizedUser)));
        adapter = new UserAdapter(authorizedUser);
        binding.recycler.setAdapter(adapter);
        binding.refreshLayout.setOnRefreshListener(() -> downloadLeaders());
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
            binding.tvPoints.setText(format(leaderboard.get(0).getScore()) + " м2");
            leaderboard.remove(0);
            adapter.setUserList(leaderboard);
            binding.refreshLayout.setRefreshing(false);
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
    private String format(long number){
        String[] chars = String.valueOf(number).split("");
        StringBuilder builder = new StringBuilder();
        for(int i = chars.length - 1; i >= 0; i--){
            if((chars.length - i - 1) % 3 == 0)
                builder.append(" ");
            builder.append(chars[i]);
        }
        return builder.reverse().toString();
    }
}