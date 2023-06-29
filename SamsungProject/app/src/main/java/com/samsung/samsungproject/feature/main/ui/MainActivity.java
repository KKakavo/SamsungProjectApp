package com.samsung.samsungproject.feature.main.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.samsung.samsungproject.R;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getApplicationContext().deleteDatabase(AppReaderContract.DATABASE_NAME);
        setTheme(R.style.Theme_SamsungProject);
    }

}