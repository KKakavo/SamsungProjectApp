package com.samsung.samsungproject.feature.login.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {


    private FragmentLoginBinding binding;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);
        binding.logAcbRegister.setOnClickListener(v -> Navigation.findNavController(binding.getRoot())
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()));
        binding.logAcbSubmit.setOnClickListener(v -> {
            UserRepository.getUserByEmail(binding.logEtEmail.getText().toString())
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                User user = new User(response.body().getEmail(),
                                        response.body().getNickname(),
                                        response.body().getPassword(),
                                        response.body().getRole());
                                BCrypt.Result result = BCrypt.verifyer().verify(binding.logEtPassword.getText().toString().toCharArray(), user.getPassword().toCharArray());
                                if (result.verified)
                                    Navigation.findNavController(binding.getRoot())
                                            .navigate(LoginFragmentDirections.actionLoginFragmentToMapFragment());
                                else
                                    Toast.makeText(requireContext(), "wrong password", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e("TAG", t.getMessage());
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