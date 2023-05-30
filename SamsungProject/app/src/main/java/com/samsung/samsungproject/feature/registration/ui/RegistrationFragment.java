package com.samsung.samsungproject.feature.registration.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.api.RetrofitService;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentRegistrationBinding;
import com.samsung.samsungproject.domain.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;
    private String LOG_TAG = "RegistrationFragment";
    Resources.Theme theme;

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater);
        theme = binding.getRoot().getContext().getTheme();
        binding.regTvLogin.setOnClickListener(v -> Navigation.findNavController(binding.getRoot())
                .navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()));
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TypedValue bt_background = new TypedValue();
                TypedValue bt_text = new TypedValue();
                if (binding.regEtEmail.getText().length()>0
                        && binding.regEtPassword.getText().length()>0
                && binding.regEtNickname.getText().length()>0){
                    theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, bt_background, true);
                    theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, bt_text, true);
                    binding.regAcbEnter.setEnabled(true);
                }
                else {
                    theme.resolveAttribute(com.google.android.material.R.attr.colorButtonNormal, bt_background, true);
                    theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, bt_text, true);
                    binding.regAcbEnter.setEnabled(false);
                }
                binding.regAcbEnter.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), bt_background.resourceId));
                binding.regAcbEnter.setTextColor(ContextCompat.getColorStateList(requireContext(), bt_text.resourceId));
            }
        };
        binding.regEtNickname.addTextChangedListener(textWatcher);
        binding.regEtEmail.addTextChangedListener(textWatcher);
        binding.regEtPassword.addTextChangedListener(textWatcher);
        binding.regAcbEnter.setOnClickListener(v -> {
            binding.regEtNickname.setEnabled(false);
            binding.regEtPassword.setEnabled(false);
            binding.regEtEmail.setEnabled(false);
            binding.regAcbEnter.setEnabled(false);
            UserRepository.saveUser(new User(0,binding.regEtEmail.getText().toString(),
                    binding.regEtNickname.getText().toString(),
                    binding.regEtPassword.getText().toString(),
                    "user", 0)).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        User user = response.body();
                        User.saveUserToPreferences(requireActivity(), user.getEmail(), user.getPassword());
                        RetrofitService.addCredentials(user.getEmail(), user.getPassword());
                        Navigation.findNavController(binding.getRoot())
                                .navigate(RegistrationFragmentDirections.actionRegistrationFragmentToMapFragment(user));
                    } else if (response.code() == 400){
                        binding.tvError.setText(R.string.email_registered);
                        binding.tvError.setVisibility(View.VISIBLE);
                    }
                    binding.regEtNickname.setEnabled(true);
                    binding.regEtPassword.setEnabled(true);
                    binding.regEtEmail.setEnabled(true);
                    binding.regAcbEnter.setEnabled(true);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(LOG_TAG, t.getMessage());
                    binding.tvError.setText(R.string.server_not_responding);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.regEtNickname.setEnabled(true);
                    binding.regEtPassword.setEnabled(true);
                    binding.regEtEmail.setEnabled(true);
                    binding.regAcbEnter.setEnabled(true);
                }
            });
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}