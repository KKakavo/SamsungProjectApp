package com.samsung.samsungproject.feature.registration.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentRegistrationBinding;
import com.samsung.samsungproject.domain.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;
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
        binding.regAcbSubmit.setOnClickListener(v -> {
            UserRepository.saveUser(new User(binding.regEtEmail.getText().toString(),
                    binding.regEtNickname.getText().toString(),
                    binding.regEtPassword.getText().toString(),
                    "user")).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(binding.getRoot())
                            .navigate(RegistrationFragmentDirections.actionRegistrationFragmentToMapFragment());
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(requireContext(), "failure", Toast.LENGTH_LONG).show();
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