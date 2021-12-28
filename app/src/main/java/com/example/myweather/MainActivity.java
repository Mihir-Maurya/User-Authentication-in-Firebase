package com.example.myweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
 private EditText mloginEmail,mloginPassword;
 private RelativeLayout mlogin,msignup;
 private TextView mgotoForgetPassword;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mloginEmail = findViewById(R.id.loginemail);
        mloginPassword = findViewById(R.id.loginpassword);
        mlogin = findViewById(R.id.login);
        msignup=findViewById(R.id.signup);
        mgotoForgetPassword = findViewById(R.id.gotoforgetpassword);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            finish();
            startActivity(new Intent(MainActivity.this,NotesActivity.class));
        }
        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Signup.class));
            }
        });
        mgotoForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgetPassword.class));
            }
        });
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mloginEmail.getText().toString().trim();
                String password = mloginPassword.getText().toString().trim();
                if(mail.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All Fields ar Required",Toast.LENGTH_SHORT).show();
                }
                else{
                    //login the user
                     firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if(task.isSuccessful()){
                                 checkMailVerfication();
                             }else{
                                 Toast.makeText(getApplicationContext(),"Account Doesn't Exists",Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                }
            }
        });
    }
    private void checkMailVerfication(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()==true){
            Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(MainActivity.this,NotesActivity.class));
        }else{
            Toast.makeText(getApplicationContext(),"Verify Your Email First",Toast.LENGTH_SHORT).show();
           firebaseAuth.signOut();
        }
    }
}