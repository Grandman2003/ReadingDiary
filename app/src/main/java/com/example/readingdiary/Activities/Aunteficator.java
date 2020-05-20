package com.example.readingdiary.Activities;

import android.content.Context;
import android.content.SharedPreferences;

public class Aunteficator {
    SharedPreferences sharedPreferences; // getSharedPreferences("gameSetting", Context.MODE_PRIVATE);
    int line = sharedPreferences.getInt("User", 0);

    public void authIN()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("User", 1);
        editor.apply();
    }
    public void authON()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("User", 0);
        editor.apply();
    }
}
