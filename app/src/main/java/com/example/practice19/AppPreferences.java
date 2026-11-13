package com.example.practice19;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LOGIN = "Login";
    public static final String APP_PREFERENCES_PASSWORD = "Password";

    private SharedPreferences mSettings;

    public AppPreferences(Context context) {
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveLoginAndPassword(String login, String password) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_LOGIN, login);
        editor.putString(APP_PREFERENCES_PASSWORD, password);
        editor.apply();
    }

    public String getLogin() {
        return mSettings.getString(APP_PREFERENCES_LOGIN, "");
    }

    public String getPassword() {
        return mSettings.getString(APP_PREFERENCES_PASSWORD, "");
    }
}