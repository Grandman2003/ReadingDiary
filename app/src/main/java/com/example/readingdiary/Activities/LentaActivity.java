package com.example.readingdiary.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LentaActivity extends AppCompatActivity {
    Button bUpdateLent;
    RecyclerView rvPosts;
    PostAdapter postAdapter;
    ProgressBar progressBar;
    ArrayList<String> subUsersId;
    String user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<RealNote> notes=new ArrayList<>();
    ArrayList<String[]> notesRef=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);
        bUpdateLent = findViewById(R.id.bUpdateLent);
        rvPosts = findViewById(R.id.rvPosts);
        progressBar =findViewById(R.id.progressBar);
//        ArrayList<Note> notes;
//        list = new ArrayList<>(); // можешь назвать подругому, выбрать другой тип.
        // Если будешь менять тип данных, то не забудь про адаптер
        postAdapter = new PostAdapter(notes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        rvPosts.setAdapter(postAdapter);
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setItemAnimator(itemAnimator);
        progressBar.setVisibility(View.INVISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Subscriptions").document(user).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                         subUsersId = new ArrayList<String>(documentSnapshot.getData().keySet());
                        showNotes();
                    }
                });

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(LentaActivity.this, NoteActivity.class);
                intent.putExtra("security", "guest");
                intent.putExtra("owner", notes.get(position).getOwner());
                intent.putExtra("id", notes.get(position).getID());
                startActivity(intent);
            }
        });




        bUpdateLent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notes.clear();
                notesRef.clear();
                postAdapter.notifyDataSetChanged();

              // if onCompete progressBar.setVisibility(View.GONE);
                //для каждой записи нужно сделать отдельную разметку
                // будем отображать только 20 последних записей
                int allsub = 1; // переменная, в которую мы передаём количество подписок
                    // вывод этих записей в ленту
//                list.add(new RealNote("1", "qwertyu", "qwertyu", "qwertyu", 3));
//                Toast.makeText(getApplicationContext(), list.size()+"", 1).show();
//                list.add(new RealNote("1", "qwertyu", "qwertyu", "qwertyu", 3));
//                post.notifyItemInserted(list.size()-1);
//                post.onCreateViewHolder(rvPosts,allsub);
                chooseNotes();


            }
        });




    }

    public void showNotes(){
        for (final String[] arr : notesRef){
            db.collection("Notes").document(arr[0]).collection("userNotes").document(arr[1]).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            final HashMap map = (HashMap) documentSnapshot.getData();
                            if (map != null){
//                                ArrayList<Double> publicRatingArray
//                                Double sumRating = map.get("publicRatingSum");
//                                int countRating = map.get("publicRatingCount");

                                final RealNote realNote = new RealNote(arr[1], map.get("path").toString(), map.get("author").toString(),
                                        map.get("title").toString(), Double.valueOf(map.get("rating").toString()), (boolean)map.get("private"), (double)map.get("publicRatingSum"), (long)map.get("publicRatingCount"));
                                realNote.setOwner(arr[0]);
                                Log.d("qwerty48", "qwerty0");
                                if (map.get("imagePath")!= null && !map.get("imagePath").toString().equals("")){
                                    Log.d("qwerty48", "qwerty1");
                                    FirebaseStorage.getInstance().getReference(arr[0]).child(arr[1]).child("Images").child(map.get("imagePath").toString()).getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.d("qwerty48", "qwerty2");
                                                    realNote.setCoverPath(uri);
                                                    notes.add(realNote);
                                                    postAdapter.notifyItemInserted(notes.size()-1);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                        notes.add(realNote);
                                                        postAdapter.notifyItemInserted(notes.size()-1);
                                                }
                                            });
                                }
                                else{
                                        notes.add(realNote);
                                        postAdapter.notifyItemInserted(notes.size()-1);

                                    }
                                }
                            }
                        });
        }


    }

    public void chooseNotes(){
        final TreeMap<Long, ArrayList<String[]>> treeMap = new TreeMap<>();
        final int[] arr = {0, subUsersId.size()};
        for (final String subUserId : subUsersId){
            db.collection("Publicly").document(subUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    HashMap<String, String> map = (HashMap) documentSnapshot.getData();
                    for (String key : map.keySet()){
                        if (treeMap.containsKey(-Long.parseLong(key))){
                            treeMap.get(-Long.parseLong(key)).add(new String[]{subUserId, map.get(key)});
                        }
                        else{
                            ArrayList<String[]> arrayList = new ArrayList<>();
                            arrayList.add(new String[]{subUserId, map.get(key)});
                            treeMap.put(-Long.parseLong(key), arrayList);
                        }
                    }

                    arr[0]++;
                    if (arr[0] == arr[1]){
                        int count=0;
                        for (Long key : treeMap.keySet()){
                            for (String[] q : treeMap.get(key)){
                                notesRef.add(q);
                                count++;
                                if (count >= 20){
                                    break;
                                }
                            }
                        }
                        showNotes();

                    }
                }
            });
        }
    }


}
