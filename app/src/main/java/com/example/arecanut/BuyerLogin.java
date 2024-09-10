package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class BuyerLogin extends AppCompatActivity {
    private static final int RC_SIGN_IN = 120;
    GoogleSignInClient mGoogleSignInClient;
    EditText mEmail, mPassword;
    Button mLoginBtn;
    FirebaseAuth mAuth;
    CardView mGoogleSignBtn;
    RelativeLayout mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_login);

        // Call initialization and click listeners methods
        initializations();
        initializeGoogleSignin();
        clickListeners();
    }

    private void initializations() {
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.loginEmail);
        mPassword = findViewById(R.id.loginPassword);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mGoogleSignBtn = findViewById(R.id.googleSignInBtn);
    }

    private void initializeGoogleSignin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Log.d("GoogleSignIn", "Google Sign-In Client initialized successfully.");
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void clickListeners() {
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuyerLogin.this, BuyerRegister.class);
                startActivity(intent);
            }
        });

        mGoogleSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty())
                    Toast.makeText(BuyerLogin.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                else
                    CustomLogin(email, password);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Call the method to sign in with Firebase
            signinWithFirebase(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Log.e("GoogleSignInError", "Google Sign-In failed with code: " + e.getStatusCode());
        }
    }

    private void signinWithFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String current_uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    String name = account.getDisplayName();
                    String email = account.getEmail();
                    String profile = Objects.requireNonNull(account.getPhotoUrl()).toString();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", name);
                    hashMap.put("email", email);
                    hashMap.put("profile", profile);
                    hashMap.put("user_type", "buyer"); // Change to the appropriate user type
                    hashMap.put("online", false);

                    db.collection("Users").document(current_uid)
                            .set(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("FirebaseSignIn", "User data stored successfully.");

                                        // Navigate to BuyerHome directly
                                        Intent intent = new Intent(BuyerLogin.this, BuyerHome.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e("FirebaseSignIn", "Failed to store user data: " + task.getException());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("FirebaseSignIn", "Failed to store user data: " + e.getMessage());
                                }
                            });
                } else {
                    Log.e("FirebaseSignIn", "Firebase Sign-In failed: " + task.getException());
                }
            }
        });
    }

    private void CustomLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Navigate to BuyerHome directly
                            Intent intent = new Intent(BuyerLogin.this, BuyerHome.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        } else {
                            Toast.makeText(BuyerLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
