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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public FirebaseAuth mAuth= FirebaseAuth.getInstance();
    public FirebaseAuth.AuthStateListener mAuthListener;
    public EditText ETemail;
    public EditText ETpassword;
    public TextView tvForgPsw;
    ProgressBar progressBar2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    // Привет, зеленая обезьянка
    // Привет, работяга
    FirebaseUser currentUser ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                }
                else
                {
                }
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

           // Toast.makeText(MainActivity.this, "Offline ", Toast.LENGTH_SHORT).show();

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

    public void signin(String email , String password)
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
    public void registration(final String email, final String password) {
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
                                                    UserProfileChangeRequest u = builder.build();
                                                    userAuth.updateProfile(u);
                                                    db.runTransaction(new Transaction.Function<Long>() {
                                                        @Nullable
                                                        @Override
                                                        public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                                            Log.d("qwerty55", "qwerty");
                                                            DocumentReference documentReference = db.collection("PublicID").document("LastID");
                                                            DocumentSnapshot lastID = transaction.get(documentReference);
                                                            if (lastID != null && lastID.getLong("id")!=null){
                                                                Log.d("qwerty55", "qwerty!");
                                                                long newID = lastID.getLong("id") + 1;
                                                                transaction.update(documentReference, "id", newID);
                                                                Log.d("qwerty55", "qwerty!" + newID);
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
                                                        }
                                                    });
                                                }
                                            });
                                    Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                                    startActivity(intent);

//                                    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                    finish();
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

}


