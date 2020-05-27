package com.example.readingdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.readingdiary.Fragments.AddShortNameFragment;
import com.example.readingdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.opencensus.internal.StringUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddShortNameFragment.AddShortNameDialogListener {


    public FirebaseAuth mAuth= FirebaseAuth.getInstance();
    public FirebaseAuth.AuthStateListener mAuthListener;
    public EditText ETemail;
    public EditText ETpassword;
    public TextView tvForgPsw;
    ProgressBar progressBar2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user;
    FirebaseUser currentUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {}
                else
                {}
                updateUI(user);
            }
        };

        ETemail = (EditText) findViewById(R.id.et_email);
        ETpassword = (EditText) findViewById(R.id.etForgPsw);
        findViewById(R.id.btn_ForgPsw).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);
        currentUser = mAuth.getCurrentUser();
        progressBar2 =findViewById(R.id.progressBar2);
        tvForgPsw= (TextView) findViewById(R.id.tvForgPsw);
        tvForgPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPswActivity.class);
                startActivity(intent);
            }
        });

        if (currentUser!= null)
        {
           // Toast.makeText(MainActivity.this, "Online ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
            startActivity(intent);
            //updateUI();
            currentUser=null;;

        }
        else if (currentUser==null)
        {
        }


    }


    public void updateUI(FirebaseUser user) {
//        frgEm= "His em:"+user.getEmail();
        if (user != null) {

            //textView.setVisibility(View.VISIBLE);
        } else {


        }
    }

    @Override
    public void onClick(View view) {


        if (ETemail.getText().toString().isEmpty()  && ETpassword.getText().toString().isEmpty())
        {
            Toast.makeText(MainActivity.this,"Введите логин и пароль",Toast.LENGTH_SHORT).show();
        }

        else if (ETemail.getText().toString().isEmpty())
        {
            Toast.makeText(MainActivity.this,"Введите логин",Toast.LENGTH_SHORT).show();
        }

        else if (ETpassword.getText().toString().isEmpty())
        {
            Toast.makeText(MainActivity.this,"Введите пароль",Toast.LENGTH_SHORT).show();
        }

        else
        {
            if (view.getId() == R.id.btn_ForgPsw)
            {
                signin(ETemail.getText().toString(), ETpassword.getText().toString());
            }
            else if (view.getId() == R.id.btn_registration)
            {
                registration(ETemail.getText().toString(), ETpassword.getText().toString());
            }
        }
    }

    private void signin(String email , String password)
    {

            mAuth.signInWithEmailAndPassword(email.trim(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    progressBar2.setVisibility(View.VISIBLE);
                    if (task.isSuccessful())
                    {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        progressBar2.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Aвторизация провалена", Toast.LENGTH_SHORT).show();

                    }
                }
            });
    }




    private void registration(final String email, final String password) {
//        MainActivity.this

        mAuth.createUserWithEmailAndPassword(email.trim(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar2.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().getCurrentUser()
                            .sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar2.setVisibility(View.GONE);
                                    //смотри когда пользователь регестрируется ему отправляется письмо
                                    FirebaseAuth.getInstance().getCurrentUser()
                                            .sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(MainActivity.this, "Вам отправленно ссылка на email. Для подтверждения email перейдите по ней", Toast.LENGTH_LONG).show();
                                                    final FirebaseUser userAuth = mAuth.getCurrentUser();
                                                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                                                    user = userAuth.getUid();
                                                    UserProfileChangeRequest u = builder.build();
                                                    userAuth.updateProfile(u);
                                                    db.runTransaction(new Transaction.Function<Long>() {
                                                        @Nullable
                                                        @Override
                                                        public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                            DocumentReference documentReference = db.collection("PublicID").document("LastID");
                                                            DocumentSnapshot lastID = transaction.get(documentReference);
                                                            if (lastID != null && lastID.getLong("id")!=null){
                                                                long newID = lastID.getLong("id") + 1;
                                                                transaction.update(documentReference, "id", newID);
                                                                return newID;
                                                            }
                                                            else{
                                                                Map<String, Long> map = new HashMap<>();
                                                                map.put("id", (Long)(long)0);
                                                                transaction.set(documentReference, map);
                                                                return (Long)(long)0;
                                                            }
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<Long>() {
                                                        @Override
                                                        public void onSuccess(Long aLong) {
                                                            Map<String, String> map = new HashMap<>();
                                                            map.put("id", aLong+"");
                                                            db.collection("PublicID").document(userAuth.getUid()).set(map);
                                                            AddShortNameFragment saveDialogFragment = new AddShortNameFragment(getApplicationContext());
                                                            saveDialogFragment.setCancelable(false);
                                                            FragmentManager manager = getSupportFragmentManager();
                                                            FragmentTransaction transaction = manager.beginTransaction();
                                                            saveDialogFragment.show(transaction, "dialog");
                                                        }
                                                    });
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar2.setVisibility(View.GONE);
                            Toast toast = Toast.makeText(getApplicationContext(), "Ошибка: "
                                    + e.getMessage(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }

                else
                {
                    Toast.makeText(MainActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onEnterClicked(final String name) {

        if (name.contains(" ") || name.length() > 20 || name.equals("") || android.text.TextUtils.isDigitsOnly(name)){
            Log.d("qwerty61", "hi!!!");
            AddShortNameFragment saveDialogFragment;
            if (name.contains(" ")) saveDialogFragment = new AddShortNameFragment(getApplicationContext(), "space");
            else if (name.length() > 20) saveDialogFragment = new AddShortNameFragment(getApplicationContext(), "long");
            else if (name.equals("")) saveDialogFragment = new AddShortNameFragment(getApplicationContext(), "null");
            else saveDialogFragment = new AddShortNameFragment(getApplicationContext(), "number");
            saveDialogFragment.setCancelable(false);
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            saveDialogFragment.show(transaction, "dialog");
            return;
        }


        db.runTransaction(new Transaction.Function<String>() {
            @Nullable
            @Override
            public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference documentReference = db.collection("allNames").document("allNames");
                DocumentSnapshot allNamesShapshot = transaction.get(documentReference);
                if (allNamesShapshot != null && allNamesShapshot.getString("names")!=null){
                    String names = allNamesShapshot.getString("names");
                    if (Arrays.asList(names.split(" ")).contains(name)){
                        HashMap<String, String> map = new HashMap<>();
                        map.put("names", names);
                        transaction.set(db.collection("allNames").document("allNames"), map);
                        return "";
                    }
                    else{
                        HashMap<String, String> map = new HashMap<>();
                        map.put("names", names + " " + name);
                        transaction.set(db.collection("allNames").document("allNames"), map);
                        return name;
                    }
                }
                else{
                    HashMap<String, String> map = new HashMap<>();
                    map.put("names", name);
                    transaction.set(db.collection("allNames").document("allNames"), map);
                    return name;
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String str) {
                if (str.equals("")){
                    AddShortNameFragment saveDialogFragment = new AddShortNameFragment(getApplicationContext(), "exists");
                    saveDialogFragment.setCancelable(false);
                    FragmentManager manager = getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    saveDialogFragment.show(transaction, "dialog");
                }
                else{
                    Map<String, String> map= new HashMap<>();
                    map.put("id", str);
                    db.collection("PublicID").document(user).set(map);
                    Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}


