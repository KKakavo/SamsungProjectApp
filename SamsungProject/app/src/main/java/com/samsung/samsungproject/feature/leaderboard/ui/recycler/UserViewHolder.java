package com.samsung.samsungproject.feature.leaderboard.ui.recycler;

import android.content.res.Resources.Theme;
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.samsung.samsungproject.R;
import com.samsung.samsungproject.databinding.LeaderboardItemBinding;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.leaderboard.presentation.LeaderboardUtils;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private final LeaderboardItemBinding binding;
    private final User authorizedUser;
    private final UserClickListener clickListener;

    public UserViewHolder(LeaderboardItemBinding binding, User authorizedUser, UserClickListener clickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.authorizedUser = authorizedUser;
        this.clickListener = clickListener;
    }

    public void bind(User user, int position) {
        Theme theme = binding.getRoot().getContext().getTheme();
        binding.tvPlace.setText(String.valueOf(position + 2));
        binding.tvNickname.setText("@" + user.getNickname());
        binding.tvScore.setText(LeaderboardUtils.format(user.getScore()) + " м2");
        binding.getRoot().setOnClickListener(v -> clickListener.onClick(new LatLng(user.getLatitude(), user.getLongitude())));
        TypedValue typedValue = new TypedValue();
        if (user.getEmail().equals(authorizedUser.getEmail())) {
            theme.resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true);
            if (position >= 49)
                binding.tvPlace.setText("");
        } else {
            theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        }
        binding.itemBackground.setBackgroundTintList(ContextCompat.getColorStateList(binding.getRoot().getContext(), typedValue.resourceId));
        switch (position) {
            case 0:
                binding.tvPlace.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray_BD));
                binding.tvNickname.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.gray_BD));
                binding.tvNickname.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                binding.tvNickname.setTextSize(16);

                break;
            case 1:
                binding.tvPlace.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.orange));
                binding.tvNickname.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.orange));
                binding.tvNickname.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                binding.tvNickname.setTextSize(15);
                break;
            default:
                TypedValue color = new TypedValue();
                theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, color, true);
                binding.tvPlace.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), color.resourceId));
                binding.tvNickname.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), color.resourceId));
                binding.tvNickname.setTextSize(14);
                binding.tvNickname.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                break;
        }
    }

}
