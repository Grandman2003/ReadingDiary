package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.readingdiary.Classes.QuickSort;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.PostAdapter;

public class LentaActivity extends AppCompatActivity {
    Button bUpdateLent;
    RecyclerView rvPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);

        bUpdateLent = (Button)  findViewById(R.id.bUpdateLent);
        rvPosts = (RecyclerView) findViewById(R.id.rvPosts);
        final PostAdapter post = new PostAdapter();

        bUpdateLent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //для каждой записи нужно сделать отдельную разметку
                // будем отображать только 20 последних записей
                int allsub = 1; // переменная, в которую мы передаём количество подписок
                    // вывод этих записей в ленту

                post.onCreateViewHolder(rvPosts,allsub);


            }
        });




    }
}
