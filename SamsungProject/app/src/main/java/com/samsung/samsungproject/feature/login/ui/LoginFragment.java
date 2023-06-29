package com.samsung.samsungproject.feature.login.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.databinding.FragmentLoginBinding;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.login.presentation.LoginStatus;
import com.samsung.samsungproject.feature.login.presentation.LoginViewModel;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private FragmentLoginBinding binding;
    Resources.Theme theme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.user.observe(this, this::renderUser);
        viewModel.status.observe(this, this::renderStatus);
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
        if(savedInstanceState == null){
            String email = User.getEmailFromPreferences(requireActivity());
            String password = User.getPasswordFromPreferences(requireActivity());
            if(email != null) {
                binding.logEtEmail.setText(email);
                if(password != null){
                    viewModel.sharedPreferencesLogin(email, password);
                }
            }
        }
        binding.logAcbEnter.setOnClickListener(v -> {
            disableScreen();
            viewModel.login(binding.logEtEmail.getText().toString(),
                    binding.logEtPassword.getText().toString());
        });
        return binding.getRoot();
    }
    private void renderUser(User user) {
        User.saveUserToPreferences(requireActivity(), user.getEmail(), user.getPassword());
        Navigation.findNavController(binding.getRoot())
                .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment(user));
    }
    private void renderStatus(LoginStatus status) {
        switch (status){
            case SUCCESS:
                binding.tvError.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                break;
            case LOADING:
                binding.tvError.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setEnabled(true);
                break;
            case SERVER_NOT_RESPONDING:
                binding.tvError.setText(R.string.server_not_responding);
                binding.tvError.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                enableScreen();
                break;
            case WRONG_EMAIL:
                binding.tvError.setText(R.string.wrong_email);
                binding.tvError.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                enableScreen();
                break;
            case WRONG_PASSWORD:
                binding.tvError.setText(R.string.wrong_password);
                binding.tvError.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                enableScreen();
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    private void disableScreen(){
        binding.logEtEmail.setEnabled(false);
        binding.logEtPassword.setEnabled(false);
        binding.logAcbEnter.setEnabled(false);
        binding.logTvRegistration.setEnabled(false);
    }
    private void enableScreen(){
        binding.logEtEmail.setEnabled(true);
        binding.logEtPassword.setEnabled(true);
        binding.logAcbEnter.setEnabled(true);
        binding.logTvRegistration.setEnabled(true);
    }
}