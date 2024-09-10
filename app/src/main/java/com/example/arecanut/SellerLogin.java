package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerLogin extends AppCompatActivity {

    private EditText loginView, passwordView;
    private Button loginButton;
    private TextView signupRedirectText;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellerlogin);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("sellers");

        loginView = findViewById(R.id.loginView);
        passwordView = findViewById(R.id.loginView2);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginInput = loginView.getText().toString().trim();
                String passwordInput = passwordView.getText().toString().trim();

                if (!loginInput.isEmpty() && !passwordInput.isEmpty()) {
                    // Fetch seller data from Firebase
                    fetchSellerData(loginInput, passwordInput);
                } else {
                    Toast.makeText(SellerLogin.this, "Please enter both login and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the SellerDetails page
                Intent intent = new Intent(SellerLogin.this, SellerDetails.class);
                startActivity(intent);
            }
        });
    }

    private void fetchSellerData(final String loginInput, final String passwordInput) {
        databaseReference.orderByChild("email").equalTo(loginInput).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot sellerSnapshot : snapshot.getChildren()) {
                        // Retrieve seller password from Firebase
                        String storedPassword = sellerSnapshot.child("password").getValue(String.class);

                        // Check if the entered password matches the stored password
                        if (passwordInput.equals(storedPassword)) {
                            // Password matches, login successful
                            Toast.makeText(SellerLogin.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Proceed to the next activity or perform other actions
                            // For example:
                            String sellerEmail = loginInput; // Assuming loginInput is the seller's email
                            Intent intent = new Intent(SellerLogin.this, MainActivity.class);
                            intent.putExtra("SELLER_EMAIL", sellerEmail);
                            startActivity(intent);
                        } else {
                            // Password doesn't match
                            Toast.makeText(SellerLogin.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(SellerLogin.this, "Seller not found with the provided email", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SellerLogin.this, "Error fetching seller data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
