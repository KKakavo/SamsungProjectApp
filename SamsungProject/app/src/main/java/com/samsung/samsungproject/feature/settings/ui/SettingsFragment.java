package com.samsung.samsungproject.feature.settings.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.databinding.FragmentSettingsBinding;
import com.samsung.samsungproject.domain.model.User;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater);
        binding.btBack.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
        binding.tvLeave.setOnClickListener(v -> {
            User.saveUserToPreferences(requireActivity(), null, null);
            Navigation.findNavController(binding.getRoot()).navigate(SettingsFragmentDirections.actionSettingsFragmentToLoginFragment());
        });
        return binding.getRoot();
    }
}