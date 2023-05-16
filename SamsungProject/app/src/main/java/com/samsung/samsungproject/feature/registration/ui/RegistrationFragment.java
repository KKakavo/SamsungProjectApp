package com.samsung.samsungproject.feature.registration.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.dto.UserDto;
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
        binding.regTvLogin.setOnClickListener(v -> Navigation.findNavController(binding.getRoot())
                .navigate(com.samsung.samsungproject.feature.registration.ui.RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment()));
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.regEtEmail.getText().length()>0
                        && binding.regEtPassword.getText().length()>0
                && binding.regEtNickname.getText().length()>0){
                    binding.regAcbEnter.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_33));
                    binding.regAcbEnter.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_F2));
                }
                else {
                    binding.regAcbEnter.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_BD));
                    binding.regAcbEnter.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.gray_82));
                }
            }
        };
        binding.regEtNickname.addTextChangedListener(textWatcher);
        binding.regEtEmail.addTextChangedListener(textWatcher);
        binding.regEtPassword.addTextChangedListener(textWatcher);
        binding.regAcbEnter.setOnClickListener(v -> UserRepository.saveUser(UserDto.toDto(new User(binding.regEtEmail.getText().toString(),
                binding.regEtNickname.getText().toString(),
                binding.regEtPassword.getText().toString(),
                "user", 0))).enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful()) {
                    User user = UserDto.toDomainObject(response.body());
                    User.insertUserIntoSharedPreferences(requireActivity(), user.getId(), user.getPassword());
                    Navigation.findNavController(binding.getRoot())
                            .navigate(RegistrationFragmentDirections.actionRegistrationFragmentToMapFragment(user));
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
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