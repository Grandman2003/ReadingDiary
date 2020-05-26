package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.readingdiary.Classes.QuickSort;
import com.example.readingdiary.R;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingdiary.Classes.QuickSort;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.SubscriptionsShowAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;





public class OnlineActivity extends AppCompatActivity
{

    Button bShareUser;
    Button bGoLent;
    EditText etShareUser;
    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String [] x = new String[] {"wsdsaddaads","adsdsaa ","dsadsdsad " };// тестовый массив подписок
    ArrayList<String> subscriptions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        etShareUser = (EditText) findViewById(R.id.etShareUser);//строка поиска


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        bShareUser= (Button) findViewById(R.id.bShareUser); //кнопка поиска
        bShareUser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!etShareUser.getText().toString().equals("") && etShareUser.getText().toString()!= user )
                {
                    FirebaseFirestore.getInstance().collection("PublicID").whereEqualTo("id", etShareUser.getText().toString()).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                    {
                                        Map<String, String> map = new HashMap<>();
                                        map.put(documentSnapshot.getId(), etShareUser.getText().toString());
                                        FirebaseFirestore.getInstance().collection("Subscriptions").document(user).set(map, SetOptions.merge());
                                    }
                                }
                            });
                    Toast.makeText(OnlineActivity.this, "Пользователь добавлен в подписки ", Toast.LENGTH_SHORT).show();
                }
                  else {Toast.makeText(OnlineActivity.this, "Введите корректный id пользователя", Toast.LENGTH_SHORT).show();}
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

        RecyclerView subscriptionsRecycler = findViewById(R.id.subscriptions_id_recycler);
        final SubscriptionsShowAdapter subscriptionsAdapter = new SubscriptionsShowAdapter(subscriptions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        subscriptionsRecycler.setAdapter(subscriptionsAdapter);
        subscriptionsRecycler.setLayoutManager(layoutManager);
        subscriptionsRecycler.setItemAnimator(itemAnimator);
        FirebaseFirestore.getInstance().collection("Subscriptions").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.e("onlineE", e.toString());
                }
                else{
                    subscriptions.clear();
                    subscriptionsAdapter.notifyDataSetChanged();
                    HashMap<String, String> hashMap = (HashMap) documentSnapshot.getData();
                    if (hashMap != null){
                        for (String key : hashMap.keySet()){
                            subscriptions.add(hashMap.get(key));
                            subscriptionsAdapter.notifyItemInserted(subscriptions.size());
                        }
                    }

                }
            }
        });

        subscriptionsAdapter.setOnItemClickListener(new SubscriptionsShowAdapter.OnItemClickListener() {
            @Override
            public void onRemoveSubscription(final int position) {
                FirebaseFirestore.getInstance().collection("PublicID").whereEqualTo("id", subscriptions.get(position)).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                  FirebaseFirestore.getInstance().collection("Subscriptions")
                                            .document(user).update(documentSnapshot.getId(), FieldValue.delete());
                                }
                                subscriptions.remove(position);
                                subscriptionsAdapter.notifyItemRemoved(position);
                            }
                        });
            }
        });

    }


}
