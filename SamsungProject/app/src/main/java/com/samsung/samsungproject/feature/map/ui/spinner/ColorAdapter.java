package com.samsung.samsungproject.feature.map.ui.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.samsung.samsungproject.databinding.SpinnerItemBinding;

import java.util.List;


public class ColorAdapter extends ArrayAdapter<Integer> {

    public ColorAdapter(@NonNull Context context,int resource, @NonNull List<Integer> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        SpinnerItemBinding binding = SpinnerItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        binding.colorSpinnerItem.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), getItem(position)));
        return  binding.getRoot();
    }

}

