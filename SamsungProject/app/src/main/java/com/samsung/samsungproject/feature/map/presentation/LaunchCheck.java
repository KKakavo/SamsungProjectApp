package com.samsung.samsungproject.feature.map.presentation;

import android.content.Context;
import android.content.SharedPreferences;

public class LaunchCheck {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LaunchTime";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public LaunchCheck(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
