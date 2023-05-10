package com.samsung.samsungproject.feature.login.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentLoginBinding;
import com.samsung.samsungproject.domain.model.User;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCryptFormatter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {


    private FragmentLoginBinding binding;
    private SharedPreferences sharedPreferences;
    private final static String NICKNAME_KEY = "NICKNAME_KEY";
    private final static String EMAIL_KEY = "EMAIL_KEY";
    private final static String PASSWORD_KEY = "PASSWORD_KEY";
    private final static String ID_KEY = "ID_KEY";
    private final static String ROLE_KEY = "ROLE_KEY";

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
        if(isUserSaved()){
            UserRepository.getUserByEmail(sharedPreferences.getString(EMAIL_KEY,null))
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                User user = new User(response.body().getId(),
                                        response.body().getEmail(),
                                        response.body().getNickname(),
                                        response.body().getPassword(),
                                        response.body().getRole());
                                if (sharedPreferences.getString(PASSWORD_KEY, null).equals(user.getPassword())) {
                                    Navigation.findNavController(binding.getRoot())
                                            .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment());
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
                            User user = new User(response.body().getId(),
                                    response.body().getEmail(),
                                    response.body().getNickname(),
                                    response.body().getPassword(),
                                    response.body().getRole());
                            BCrypt.Result result = BCrypt.verifyer().verify(binding.logEtPassword.getText().toString().toCharArray(), user.getPassword().toCharArray());
                            if (result.verified) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(EMAIL_KEY, user.getEmail());
                                editor.putString(NICKNAME_KEY, user.getNickname());
                                editor.putString(PASSWORD_KEY, user.getPassword());
                                editor.putString(ROLE_KEY, user.getRole());
                                editor.putLong(ID_KEY, user.getId());
                                editor.apply();
                                Navigation.findNavController(binding.getRoot())
                                        .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment());
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
    public boolean isUserSaved(){
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getLong(ID_KEY, 0) != 0;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}