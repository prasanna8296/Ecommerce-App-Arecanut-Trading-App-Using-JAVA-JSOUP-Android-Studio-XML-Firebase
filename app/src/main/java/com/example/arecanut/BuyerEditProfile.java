package com.example.arecanut;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class BuyerEditProfile extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneNumberEditText, passwordEditText;
    private TextView saveButton, logoutButton;
    private ImageView profileImageView;
    private String userEmail;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private Uri imageUri;

    private static final int GALLERY_REQUEST_CODE = 123;
    private static final String TAG = "BuyerEditProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_editprofile);

        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.textView1);
        emailEditText = findViewById(R.id.textView2);
        phoneNumberEditText = findViewById(R.id.textView3);
        passwordEditText = findViewById(R.id.textpassword);
        saveButton = findViewById(R.id.signup_button);
        logoutButton = findViewById(R.id.logout);
        profileImageView = findViewById(R.id.imageView2);

        Intent intent = getIntent();
        if (intent != null) {
            userEmail = intent.getStringExtra("email");
            if (userEmail != null) {
                retrieveUserInfo(userEmail);
            }
        }

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                currentUser = mAuth.getCurrentUser();
                startActivity(new Intent(BuyerEditProfile.this, selection.class));
                finish();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void retrieveUserInfo(String email) {
        String sanitizedEmail = email.replace(".", "_");
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(sanitizedEmail);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phoneNumber = snapshot.child("phone").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);

                    nameEditText.setText(name != null ? name : "");
                    emailEditText.setText(email != null ? email : "");
                    phoneNumberEditText.setText(phoneNumber != null ? phoneNumber : "");
                    passwordEditText.setText(password != null ? password : "");

                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        loadProfileImage(profileImageUrl);
                    }
                } else {
                    Log.d(TAG, "No such user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void updateUserInfo() {
        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPhoneNumber = phoneNumberEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        userRef.child("name").setValue(newName);
        userRef.child("email").setValue(newEmail);
        userRef.child("phone").setValue(newPhoneNumber);
        userRef.child("password").setValue(newPassword);

        if (currentUser != null && !newPassword.isEmpty()) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        } else {
                            Log.w(TAG, "Error updating password", task.getException());
                        }
                    });
        }

        if (imageUri != null) {
            uploadProfileImage(imageUri);
        } else {
            Toast.makeText(BuyerEditProfile.this, "Error: No image selected", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No image selected");
        }
    }

    private void uploadProfileImage(Uri imageUri) {
        if (currentUser != null && userRef != null) {
            String imageName = "profile_image" + currentUser.getUid();
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + imageName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Log.d(TAG, "Image download URL: " + imageUrl);

                        // Update profile image URL in database
                        userRef.child("profileImageUrl").setValue(imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(BuyerEditProfile.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Profile image URL saved successfully: " + imageUrl);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(BuyerEditProfile.this, "Error updating profile image URL", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error updating profile image URL", e);
                                });
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(BuyerEditProfile.this, "Error uploading image to Firebase Storage", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error uploading image to Firebase Storage", e);
                    });
        } else {
            Toast.makeText(BuyerEditProfile.this, "Error: currentUser or userRef is null", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: currentUser or userRef is null");
        }
    }

    private void loadProfileImage(String imageUrl) {
        Picasso.get().load(imageUrl).into(profileImageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Profile image loaded successfully");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading profile image", e);
            }
        });
    }
}