package com.samsung.samsungproject.feature.leaderboard.ui.recycler;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.databinding.LeaderboardItemBinding;
import com.samsung.samsungproject.domain.model.User;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private final LeaderboardItemBinding binding;
    private User authorizedUser;
    public UserViewHolder(LeaderboardItemBinding binding, User authorizedUser) {
        super(binding.getRoot());
        this.binding = binding;
        this.authorizedUser = authorizedUser;
    }
    public void bind(User user, int position){
        binding.tvPlace.setText(String.valueOf(position + 2));
        binding.tvNickname.setText("@" + user.getNickname());
        binding.tvScore.setText(format(user.getScore()) + " м2");
        if (user.getEmail().equals(authorizedUser.getEmail())) {
            binding.itemBackground.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), R.color.white));
            if(position>=49)
                binding.tvPlace.setText("");
        }
        switch (position){
            case 0:
                binding.tvPlace.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray_BD));
                binding.tvNickname.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray_BD));
                break;
            case 1:
                binding.tvPlace.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.orange));
                binding.tvNickname.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.orange));
                break;
        }
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
