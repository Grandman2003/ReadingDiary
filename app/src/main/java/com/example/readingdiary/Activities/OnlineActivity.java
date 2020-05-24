package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.readingdiary.Classes.QuickSort;
import com.example.readingdiary.R;

import java.util.ArrayList;

public class OnlineActivity extends AppCompatActivity
{

    Button bShareUser;
    Button bGoLent;
    EditText etShareUser;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        etShareUser = (EditText) findViewById(R.id.etShareUser);//строка поиска





        bShareUser= (Button) findViewById(R.id.bShareUser); //кнопка поиска
        bShareUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Intent intent = new Intent(OnlineActivity.this, OnlineActivity.class);
//                startActivity(intent);
                ShareUser(etShareUser.getText().toString());
                // добавление usID к списку подписок
                Toast.makeText(OnlineActivity.this,"Пользователь добавлен в подписки",Toast.LENGTH_SHORT).show();
            }
        });

        bGoLent = (Button)findViewById(R.id.bGoLent); // переход в ленту
        bGoLent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(OnlineActivity.this, LentaActivity.class);
                startActivity(intent);
            }
        });

    }

    public String ShareUser(String usID) // usID искомый user
    {

        //обращение к списку id, на которые подписан usID
        String[] ids = new String[] {""," "," " }; // массив подписок
        ArrayList <String> gotcha = new ArrayList<>(); // список совпадений
        QuickSort Share = new QuickSort(); // класс сортировки
        Share.StrSort(ids,usID,gotcha); // сортировка и поиск совпадений
        usID = String.valueOf(gotcha); // поскльку совпадение может быть только одно записываем его в str для добавления


        return usID;
    }

}
