package com.samsung.samsungproject.feature.login.ui;

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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.api.RetrofitService;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentLoginBinding;
import com.samsung.samsungproject.domain.model.User;

import java.util.List;

import at.favre.lib.crypto.bcrypt.BCrypt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {


    private FragmentLoginBinding binding;
    private String LOG_TAG = "LoginFragment";
    Resources.Theme theme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);
        theme = binding.getRoot().getContext().getTheme();
        binding.logTvRegistration.setOnClickListener(v -> Navigation.findNavController(binding.getRoot())
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()));
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
                if (binding.logEtEmail.getText().length()>0
                        && binding.logEtPassword.getText().length()>0){
                    theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, bt_background, true);
                    theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, bt_text, true);
                    binding.logAcbEnter.setEnabled(true);
                }
                else {
                    theme.resolveAttribute(com.google.android.material.R.attr.colorButtonNormal, bt_background, true);
                    theme.resolveAttribute(com.google.android.material.R.attr.colorOnSecondary, bt_text, true);
                    binding.logAcbEnter.setEnabled(false);
                }
                binding.logAcbEnter.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), bt_background.resourceId));
                binding.logAcbEnter.setTextColor(ContextCompat.getColorStateList(requireContext(), bt_text.resourceId));
            }
        };
        binding.logEtEmail.addTextChangedListener(textWatcher);
        binding.logEtPassword.addTextChangedListener(textWatcher);
        String email = User.getEmailFromPreferences(requireActivity());
        if(email != null){
            binding.logEtEmail.setEnabled(false);
            binding.logEtPassword.setEnabled(false);
            binding.logAcbEnter.setEnabled(false);
            UserRepository.getUserByEmail(email)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                User user = response.body();
                                if (User.getPasswordFromPreferences(requireActivity()).equals(user.getPassword())) {
                                    RetrofitService.addCredentials(user.getEmail(), user.getPassword());
                                    Navigation.findNavController(binding.getRoot())
                                            .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment(user));
                                }
                            }
                            binding.logEtEmail.setEnabled(true);
                            binding.logEtPassword.setEnabled(true);
                            binding.logAcbEnter.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e(LOG_TAG, t.getMessage());
                            binding.tvError.setText(R.string.server_not_responding);
                            binding.tvError.setVisibility(View.VISIBLE);
                            binding.logEtEmail.setEnabled(true);
                            binding.logEtPassword.setEnabled(true);
                            binding.logAcbEnter.setEnabled(true);
                        }
                    });
        }
        binding.logAcbEnter.setOnClickListener(v -> {
            binding.logEtEmail.setEnabled(false);
            binding.logEtPassword.setEnabled(false);
            binding.logAcbEnter.setEnabled(false);
            UserRepository.getUserByEmail(binding.logEtEmail.getText().toString())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User user = response.body();
                            BCrypt.Result result = BCrypt.verifyer().verify(binding.logEtPassword.getText().toString().toCharArray(), user.getPassword().toCharArray());
                            if (result.verified) {
                                User.saveUserToPreferences(requireActivity(), user.getEmail(), user.getPassword());

                                Navigation.findNavController(binding.getRoot())
                                        .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment(user));
                            }
                            else {
                                binding.tvError.setText(R.string.wrong_password);
                                binding.tvError.setVisibility(View.VISIBLE);
                            }
                        } else if (response.code() == 400){
                            binding.tvError.setText(R.string.wrong_email);
                            binding.tvError.setVisibility(View.VISIBLE);
                        }
                        binding.logEtEmail.setEnabled(true);
                        binding.logEtPassword.setEnabled(true);
                        binding.logAcbEnter.setEnabled(true);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(LOG_TAG, t.getMessage());
                        binding.tvError.setText(R.string.server_not_responding);
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.logEtEmail.setEnabled(true);
                        binding.logEtPassword.setEnabled(true);
                        binding.logAcbEnter.setEnabled(true);
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