package com.samsung.samsungproject.feature.login.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentLoginBinding;
import com.samsung.samsungproject.domain.model.User;

import at.favre.lib.crypto.bcrypt.BCrypt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {


    private FragmentLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);
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
                if (binding.logEtEmail.getText().length()>0
                        && binding.logEtPassword.getText().length()>0){
                    binding.logAcbEnter.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_33));
                    binding.logAcbEnter.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_F2));
                }
                else {
                    binding.logAcbEnter.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_BD));
                    binding.logAcbEnter.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_82));
                }
            }
        };
        binding.logEtEmail.addTextChangedListener(textWatcher);
        binding.logEtPassword.addTextChangedListener(textWatcher);
        long id = User.getIdFromPreferences(requireActivity());
        if(id != 0){
            UserRepository.getUserById(id)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                User user = response.body();
                                if (User.getPasswordFromPreferences(requireActivity()).equals(user.getPassword())) {
                                    Navigation.findNavController(binding.getRoot())
                                            .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment(user));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e("TAG", t.getMessage());
                        }
                    });
        }
        binding.logAcbEnter.setOnClickListener(v -> UserRepository.getUserByEmail(binding.logEtEmail.getText().toString())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User user = response.body();
                            BCrypt.Result result = BCrypt.verifyer().verify(binding.logEtPassword.getText().toString().toCharArray(), user.getPassword().toCharArray());
                            if (result.verified) {
                                User.saveUserToPreferences(requireActivity(), user.getId(), user.getPassword());
                                Navigation.findNavController(binding.getRoot())
                                        .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment(user));
                            }
                            else
                                Toast.makeText(requireContext(), "wrong password", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e("TAG", t.getMessage());
                    }
                }));
        return binding.getRoot();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}