package com.samsung.samsungproject.feature.main.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.api.RetrofitService;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.domain.db.AppReaderContract;
import com.samsung.samsungproject.domain.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getApplicationContext().deleteDatabase(AppReaderContract.DATABASE_NAME);
        setTheme(R.style.Theme_SamsungProject);
    }

}