package com.example.arecanut;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {

                } else {
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

                // Get the user type from the previous intent
                Intent intent1 = getIntent();
                String userType = intent1.getStringExtra("userType");

                // Pass the user type to the SignupActivity
                intent.putExtra("userType", userType);

                startActivity(intent);
            }
        });

    }

    public Boolean validateUsername() {
        String val = loginUsername.getText().toString().trim();
        if (TextUtils.isEmpty(val)) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString().trim();
        if (TextUtils.isEmpty(val)) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }


        // ... Existing code ...

    public void checkUser() {
        String userEmail = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference referenceUsers = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabaseUsers = referenceUsers.orderByChild("email").equalTo(userEmail);

        checkUserDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                            // User is a buyer
                            loginUsername.setError(null);

                            String nameFromDB = userSnapshot.child("name").getValue(String.class);
                            String emailFromDB = userSnapshot.child("email").getValue(String.class);

                            Intent intent = new Intent(LoginActivity.this, BuyerHome.class);
                            intent.putExtra("email", emailFromDB);
                            startActivity(intent);
                            return; // Exit method after starting the activity
                        } else {
                            loginPassword.setError("Invalid Credentials");
                            loginPassword.requestFocus();
                            return; // Exit method if password is incorrect
                        }
                    }
                }

                // If user is not in the "users" node, check in the "sellers" node
                DatabaseReference referenceSellers = FirebaseDatabase.getInstance().getReference("sellers");
                Query checkUserDatabaseSellers = referenceSellers.orderByChild("email").equalTo(userEmail);

                checkUserDatabaseSellers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot sellerSnapshot) {
                        if (sellerSnapshot.exists()) {
                            for (DataSnapshot sellerSnapshotInner : sellerSnapshot.getChildren()) {
                                String passwordFromDB = sellerSnapshotInner.child("password").getValue(String.class);

                                if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                                    // User is a seller
                                    loginUsername.setError(null);

                                    String nameFromDB = sellerSnapshotInner.child("name").getValue(String.class);
                                    String emailFromDB = sellerSnapshotInner.child("email").getValue(String.class);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("email", emailFromDB);
                                    startActivity(intent);
                                    return; // Exit method after starting the activity
                                } else {
                                    loginPassword.setError("Invalid Credentials");
                                    loginPassword.requestFocus();
                                    return; // Exit method if password is incorrect
                                }
                            }
                        }

                        // If user is not found in both "users" and "sellers" nodes
                        loginUsername.setError("User does not exist");
                        loginUsername.requestFocus();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}

