package com.samsung.samsungproject.feature.leaderboard.ui.recycler;

import static java.lang.Math.max;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samsung.samsungproject.databinding.LeaderboardItemBinding;
import com.samsung.samsungproject.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<User> userList= new ArrayList<>();

    public UserAdapter() {
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LeaderboardItemBinding binding = LeaderboardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public void setUserList(List<User> userList){
        int count = getItemCount();
        this.userList = new ArrayList<>(userList);
        notifyItemRangeChanged(0, max(count, getItemCount()));
    }
}
