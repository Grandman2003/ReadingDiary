package com.example.readingdiary.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Aunteficator // класс для определения состояния аккаунта пользователя. при входе и выходе изменяется
{
   // MainActivity mainActivity = new MainActivity();
   SharedPreferences sPref;
    int line;
    final String SAVE_AUTOREG = "SAVE_AUTOREG";
    final String NAME_SPREF = "autoris";
    public void setShetpref( AppCompatActivity activity)
    {
        sPref = activity.getSharedPreferences(NAME_SPREF,Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(SAVE_AUTOREG, 2);
        editor.apply();

        line = sPref.getInt(SAVE_AUTOREG, 2);
    }

//здесь был Гена



    Aunteficator()
    {

    }

    public void authIN()
    {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(SAVE_AUTOREG, 2);
        editor.apply();
    }
    public void authON()
    {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(SAVE_AUTOREG, 1);
        editor.apply();
    }
}
