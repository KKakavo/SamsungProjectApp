package com.samsung.samsungproject.feature.leaderboard.ui.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samsung.samsungproject.databinding.LeaderboardItemBinding;
import com.samsung.samsungproject.domain.model.User;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private final LeaderboardItemBinding binding;
    public UserViewHolder(LeaderboardItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
    public void bind(User user, int position){
        binding.tvPlace.setText(String.valueOf(position + 2));
        binding.tvNickname.setText("@" + user.getNickname());
        binding.tvScore.setText(user.getScore() + " м2");
    }
}
