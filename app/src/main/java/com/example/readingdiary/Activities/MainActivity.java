package com.example.readingdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readingdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText ETemail;
    private EditText ETpassword;


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
        ETpassword = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_registration).setOnClickListener(this);
        currentUser = mAuth.getCurrentUser();

        if (currentUser!= null)
        {
            Toast.makeText(MainActivity.this, "Online ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
            startActivity(intent);
            currentUser=null;;

        }
        else if (currentUser==null)
        {

            Toast.makeText(MainActivity.this, "Offline ", Toast.LENGTH_SHORT).show();

        }


    }


    public void updateUI(FirebaseUser user) {

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
            if (view.getId() == R.id.btn_sign_in)
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
                    if (task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this, "Aвторизация успешна", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Aвторизация провалена", Toast.LENGTH_SHORT).show();

                    }
                }
            });


    }
    public void registration(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email.trim(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseAuth.getInstance().getCurrentUser()
                            .sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //смотри когда пользователь регестрируется ему отправляется письмо
                                    FirebaseAuth.getInstance().getCurrentUser()
                                            .sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(MainActivity.this, "Вам отправленно ссылка на email. Для подтверждения email перейдите по ней", Toast.LENGTH_LONG).show();
                                                    FirebaseUser userAuth = mAuth.getCurrentUser();
                                                    UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                                                    UserProfileChangeRequest u = builder.build();
                                                    userAuth.updateProfile(u);
                                                }
                                            });
                                    Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast = Toast.makeText(getApplicationContext(), "sign-up is unsuccessful: "
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


