package com.samsung.samsungproject.feature.leaderboard.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentLeaderboardBinding;
import com.samsung.samsungproject.data.db.dao.user.UserDao;
import com.samsung.samsungproject.data.db.dao.user.UserDaoSqlite;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.leaderboard.presentation.LeaderboardStatus;
import com.samsung.samsungproject.feature.leaderboard.presentation.LeaderboardUtils;
import com.samsung.samsungproject.feature.leaderboard.presentation.LeaderboardViewModel;
import com.samsung.samsungproject.feature.leaderboard.ui.recycler.UserAdapter;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = LeaderboardFragmentArgs.fromBundle(requireArguments());
        authorizedUser = args.getUser();
        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
        viewModel.leaderboard.observe(this, this::renderLeaderboard);
        viewModel.status.observe(this, this::renderStatus);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater);
        binding.btBack.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
        adapter = new UserAdapter(authorizedUser);
        binding.recycler.setAdapter(adapter);
        binding.refreshLayout.setOnRefreshListener(viewModel::load);
        viewModel.load();
        return binding.getRoot();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    private void renderStatus(LeaderboardStatus status){
        switch (status){
            case LOADING:
                binding.refreshLayout.setRefreshing(true);
                break;
            case SUCCESS:
                binding.refreshLayout.setRefreshing(false);
                break;
            case FAILURE:
                Toast.makeText(requireContext(), R.string.server_not_responding, Toast.LENGTH_LONG).show();
                binding.refreshLayout.setRefreshing(false);
                break;
        }
    }
    private void renderLeaderboard(List<User> leaderboard){
        binding.tvNickname.setText("@" + leaderboard.get(0).getNickname());
        binding.tvPoints.setText(LeaderboardUtils.format(leaderboard.get(0).getScore()) + " м2");
        leaderboard.remove(0);
        adapter.setUserList(leaderboard);
    }

}