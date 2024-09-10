package com.example.arecanut;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupPhone, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference usersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_Phone);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);



        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate input
                if (validateInput()) {
                    // Check for unique email
                    checkEmailAvailability();
                }
            }
        });
    }

    private void checkEmailAvailability() {
        final String email = signupEmail.getText().toString().trim();

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("MyUsers");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists
                    Toast.makeText(SignupActivity.this, "Email already exists. Choose another one.", Toast.LENGTH_SHORT).show();
                } else {
                    // Email is unique, proceed with signup
                    Log.d("SignupActivity", "Email is unique. Proceeding with signup.");
                    signupUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log the error to identify the issue
                Log.e("SignupActivity", "Check email availability onCancelled: " + databaseError.getMessage());
                // Handle error
            }
        });
    }



    private boolean validateInput() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String phone = signupPhone.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate name: at least 3 letters with no digits
        if (!name.matches("[a-zA-Z]{3,}")) {
            Toast.makeText(SignupActivity.this, "Enter a valid name ", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate email using the built-in Patterns class
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignupActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate phone number: 10 digits
        if (!phone.matches("[7896]\\d{9}")) {
            Toast.makeText(SignupActivity.this, "Enter a valid 10-digit phone number ", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate password: at least 6 characters followed by a single digit
        if (!password.matches(".{6,}")) {
            Toast.makeText(SignupActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void signupUser() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String phone = signupPhone.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        HelperClass2 helperClass = new HelperClass2(name, phone, email, password);

        // Create the "MyUsers" node if it doesn't exist
//        database.getReference().child("MyUsers").setValue(true);

        // Saving the user to the general MyUsers node
        database.getReference("MyUsers").child(email.replace(".", "_")).setValue(helperClass);

        // Determine user type based on intent
        Intent intent = getIntent();
        String userType = intent.getStringExtra("userType");

        // Reference to the specific node based on user type
        DatabaseReference specificRef;
        if ("seller".equals(userType)) {
            specificRef = database.getReference("sellers");
        } else {

            specificRef = database.getReference("users");
            Log.d("SignupActivity", "User type: " + userType);
        }

        // Saving the user to the specific node
        specificRef.child(email.replace(".", "_")).setValue(helperClass);

        Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();

        // After successful signup, navigate to the next page
        Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        // Optional: Finish the current activity to prevent going back to it using the back button.
    }

}
