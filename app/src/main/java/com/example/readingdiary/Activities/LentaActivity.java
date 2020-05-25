package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingdiary.Classes.Note;
import com.example.readingdiary.Classes.QuickSort;
import com.example.readingdiary.Classes.RealNote;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class LentaActivity extends AppCompatActivity {
    Button bUpdateLent;
    RecyclerView rvPosts;
    List<Note> list;
    PostAdapter post;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);

        bUpdateLent = findViewById(R.id.bUpdateLent);
        rvPosts = findViewById(R.id.rvPosts);
        progressBar =findViewById(R.id.progressBar);
//        ArrayList<Note> notes;
        list = new ArrayList<>(); // можешь назвать подругому, выбрать другой тип.
        // Если будешь менять тип данных, то не забудь про адаптер
        post = new PostAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rvPosts.setAdapter(post);
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setItemAnimator(itemAnimator);

        progressBar.setVisibility(View.INVISIBLE);
//        progressBar.setVisibility(View.VISIBLE);

        bUpdateLent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // if onCompete progressBar.setVisibility(View.GONE);
                //для каждой записи нужно сделать отдельную разметку
                // будем отображать только 20 последних записей
                int allsub = 1; // переменная, в которую мы передаём количество подписок
                    // вывод этих записей в ленту
                list.add(new RealNote("1", "qwertyu", "qwertyu", "qwertyu", 3));
//                Toast.makeText(getApplicationContext(), list.size()+"", 1).show();
//                list.add(new RealNote("1", "qwertyu", "qwertyu", "qwertyu", 3));
                post.notifyItemInserted(list.size()-1);
//                post.onCreateViewHolder(rvPosts,allsub);


            }
        });




    }


}
