package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.readingdiary.Classes.QuickSort;
import com.example.readingdiary.R;

public class LentaActivity extends AppCompatActivity {
    TextView tLenta;
    Button bUpdateLent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);

        tLenta =(TextView) findViewById(R.id.tLenta);
        bUpdateLent = (Button)  findViewById(R.id.bUpdateLent);

        int testLen = 1000; // количпество чисел, которым заполять массив
        final int [] array = new int[testLen];
        for (int i = 0; i < testLen; i++) // заполнение array случайными числами
        {
            array[i] = (int)Math.round(Math.random() * 100);
            tLenta.append(array[i]+" ");
        }

        bUpdateLent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tLenta.setText("");
                //для каждой записи нужно сделать отдельную разметку
                // будем отображать только 20 последних записей
                int allsub = 0; // переменная, в которую мы передаём количество подписок
                // int[] subscriptions = new int[allsub]; // массив, в который мы передаём по 20 id последних записи с каждой подписки
                QuickSort share = new QuickSort();
                share.quickSort(array, 0, array.length - 1); // сортировка по id, чтоб отобразить найти 20 записй сделанных последними
                for (int zap = -1; zap<19; zap++) //начинаем с -1, чтоб вывести нулевой элемент
                {
                    // вывод этих записей в ленту
//            TextView x = share[zap];
                    tLenta.append(share.array[zap]+" ");

                }


            }
        });




    }
}
