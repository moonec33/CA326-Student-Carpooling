package com.example.student_carpooling;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText Email;
    private Button ResetPassword, Cancel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Email = findViewById(R.id.resetEmail);
        ResetPassword = findViewById(R.id.reset_password);
        Cancel = findViewById(R.id.cancel);
        mAuth = FirebaseAuth.getInstance();


        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this, Login.class);
                startActivity(intent);
            }
        });

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = Email.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please Enter an email", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPassword.this, "Email has been sent.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPassword.this, Login.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ResetPassword.this, "Email is not registered successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }



            }
        });


    }}