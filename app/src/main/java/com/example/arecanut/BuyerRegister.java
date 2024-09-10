package com.example.arecanut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class BuyerRegister extends AppCompatActivity {
    EditText mEmail, mPassword, mFullName;
    RelativeLayout mLoginBtn;
    Button mRegisterBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_register);
        initializations();
        clickListeners();
    }

    private void initializations() {
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.registerEmail);
        mFullName = findViewById(R.id.registerName);
        mPassword = findViewById(R.id.registerPassword);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRegisterBtn = findViewById(R.id.registerBtn);
    }

    private void clickListeners() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyerRegister.this, BuyerLogin.class);
                startActivity(intent);
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String name = mFullName.getText().toString();
                String password = mPassword.getText().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(BuyerRegister.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(email, password, name);
                }
            }
        });
    }

    private void registerUser(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success
                            String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name", name);
                            hashMap.put("email", email);
                            hashMap.put("profile", "default");
                            hashMap.put("user_type", "staff");
                            hashMap.put("online", false);
                            hashMap.put("current_uid", user_id);

                            // Use set method instead of update
                            db.collection("Users").document(user_id)
                                    .set(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Display Toast message
                                                Toast.makeText(BuyerRegister.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                                                // Log success
                                                Log.d("RegistrationSuccess", "User registration and Firestore update successful");

                                                // Automatically redirect to BuyerHomeActivity
                                                Intent intent = new Intent(BuyerRegister.this, BuyerHome.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Registration in Firestore failed
                                                Log.e("FirestoreError", "Error writing document", task.getException());
                                                Toast.makeText(BuyerRegister.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            // Registration with FirebaseAuth failed
                            Log.w("registerError", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(BuyerRegister.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
