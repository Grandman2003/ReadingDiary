package com.example.readingdiary.Activities;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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
    String [] x = new String[] {"wsdsaddaads","adsdsaa ","dsadsdsad " };// тестовый массив подписок
    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            if (!etShareUser.getText().equals("")) {
                FirebaseFirestore.getInstance().collection("PublicID").whereEqualTo("id", etShareUser.getText().toString()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                    Map<String, String> map = new HashMap<>();
                                    map.put(documentSnapshot.getId(), etShareUser.getText().toString());
                                    FirebaseFirestore.getInstance().collection("Subscriptions").document(user).set(map, SetOptions.merge());
                                }
                            }
                        });
                Toast.makeText(OnlineActivity.this, "Пользователь добавлен в подписки ", Toast.LENGTH_SHORT).show();
            }
            else {Toast.makeText(OnlineActivity.this, "Введите id пользователя", Toast.LENGTH_SHORT).show();}
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

        final TextView subscriptions = (TextView) findViewById(R.id.subscriptions_id_view);
        FirebaseFirestore.getInstance().collection("Subscriptions").document(user).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.e("onlineE", e.toString());
                }
                else{
                    String str = "";
                    HashMap<String, String> hashMap = (HashMap) documentSnapshot.getData();
                    if (hashMap != null){
                        for (String key : hashMap.keySet()){
                            str += hashMap.get(key)+"\n";
                            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                        }
                        subscriptions.setText(str);
                    }

                }
            }
        });


    }

    public String ShareUser(String usID) // usID искомый user
    {

        //обращение к списку id, на которые подписан usID
        //String[] ids = new String[] {""," "," " }; // массив подписок
        ArrayList <String> gotcha = new ArrayList<>(); // список совпадений
        QuickSort Share = new QuickSort(); // класс сортировки
        Share.StrSort(x,usID,gotcha); // сортировка и поиск совпадений
        usID = String.valueOf(gotcha); // поскльку совпадение может быть только одно записываем его в str для добавления


        return usID;
    }

}
