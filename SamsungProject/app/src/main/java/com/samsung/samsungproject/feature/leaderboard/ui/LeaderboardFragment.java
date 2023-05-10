package com.samsung.samsungproject.feature.leaderboard.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.databinding.FragmentLeaderboardBinding;


public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater);
        return binding.getRoot();
    }
}