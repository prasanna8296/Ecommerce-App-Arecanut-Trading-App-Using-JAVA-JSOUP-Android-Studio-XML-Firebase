package com.example.arecanut;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.Selection;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SellerProfileEdit extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private Uri imageUri;
    private ImageView profileImageView;
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText,
            adharEditText, panEditText, addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editsellerprofile);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("sellers");

        // In the onCreate method or wherever needed
        String sellerEmail = getIntent().getStringExtra("email");
// Now you can use this email to fetch and display the seller's data


        // Find views
        nameEditText = findViewById(R.id.textView);
        emailEditText = findViewById(R.id.textView2);
        phoneEditText = findViewById(R.id.textView3);
        passwordEditText = findViewById(R.id.textpassword);
        adharEditText = findViewById(R.id.textView4);
        panEditText = findViewById(R.id.textView5);
        addressEditText = findViewById(R.id.textView6);

        profileImageView = findViewById(R.id.imageView2);

        // Set an onClickListener for the profile image view
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open gallery to select a new profile picture
                openGallery();
            }
        });
        // Retrieve and display initial details
        retrieveData();

        // Set an onClickListener for the Save button
        Button saveButton = findViewById(R.id.signup_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get updated data from EditText fields
                String updatedName = nameEditText.getText().toString();
                String updatedEmail = emailEditText.getText().toString();
                String updatedPhone = phoneEditText.getText().toString();
                String updatedPassword = passwordEditText.getText().toString();
                String updatedAdhar = adharEditText.getText().toString();
                String updatedPan = panEditText.getText().toString();
                String updatedAddress = addressEditText.getText().toString();

                // Update user data in Firebase
                updateUserData(updatedEmail, updatedName, updatedPhone, updatedPassword,
                        updatedAdhar, updatedPan, updatedAddress);

                Toast.makeText(SellerProfileEdit.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    // Set an onClickListener for the Logout button
    LinearLayout linearLogOut = findViewById(R.id.linearLogOut);
        linearLogOut.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logout();
        }
    });
}

    private void retrieveData() {
        // Retrieve seller's email from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            String sellerEmail = intent.getStringExtra("email");

            // Fetch seller data from Firebase based on the email
            databaseReference.orderByChild("email").equalTo(sellerEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Assuming there is only one seller with the given email
                                DataSnapshot sellerSnapshot = dataSnapshot.getChildren().iterator().next();
                                Seller seller = sellerSnapshot.getValue(Seller.class);

                                if (seller != null) {
                                    // Log the retrieved seller details for debugging
                                    Log.d("SellerProfileEdit", "Retrieved Seller: " + seller.toString());

                                    // Populate the UI fields with the fetched data
                                    nameEditText.setText(seller.getName());
                                    emailEditText.setText(seller.getEmail());
                                    phoneEditText.setText(seller.getPhone());
                                    passwordEditText.setText(seller.getPassword());
                                    adharEditText.setText(seller.getAdhar());
                                    panEditText.setText(seller.getPan());
                                    addressEditText.setText(seller.getAddress());

                                    // Load and display the profile picture
                                    loadProfilePicture(seller.getProfilePictureUrl());
                                }
                            } else {
                                // Handle the case where the email is not found in the database
                                Toast.makeText(SellerProfileEdit.this, "Email not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                            Toast.makeText(SellerProfileEdit.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle case where email is not provided
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if needed
        }
    }
    // New: Method to load and display the profile picture using Glide
    private void loadProfilePicture(String profilePictureUrl) {
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePictureUrl)
                    .into(profileImageView);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    // New: Handle the result of selecting an image from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }


    // Update user data in Firebase, including the profile picture
    private void updateUserData(String email, String name, String phone, String password,
                                String adhar, String pan, String address) {
        DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference("sellers");

        // Query to find the user with the specified email
        sellersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming there is only one user with the given email
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    String userId = userSnapshot.getKey();

                    // Update the user data, including the profile picture
                    updateUserDataInDatabase(userId, name, phone, password, adhar, pan, address);
                } else {
                    // Handle the case where the email is not found in the database
                    Toast.makeText(SellerProfileEdit.this, "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(SellerProfileEdit.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Existing code...

    // New: Method to update user data in the database, including the profile picture
    private void updateUserDataInDatabase(String userId, String name, String phone, String password,
                                          String adhar, String pan, String address) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(userId);

        // Check if the fields are not empty before updating
        if (!name.isEmpty()) {
            userRef.child("name").setValue(name);
        }
        if (!phone.isEmpty()) {
            userRef.child("phone").setValue(phone);
        }
        if (!password.isEmpty()) {
            userRef.child("password").setValue(password);
        }
        if (!adhar.isEmpty()) {
            userRef.child("adhar").setValue(adhar);
        }
        if (!pan.isEmpty()) {
            userRef.child("pan").setValue(pan);
        }
        if (!address.isEmpty()) {
            userRef.child("address").setValue(address);
        }

        // New: Update the profile picture if a new image is selected
        if (imageUri != null) {
            uploadProfilePicture(userId);
        }
    }

    // New: Upload the selected profile picture to Firebase Storage
    private void uploadProfilePicture(String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pictures/" + userId);
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the URL of the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(userId);
                        userRef.child("profilePictureUrl").setValue(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to upload the image
                    Toast.makeText(SellerProfileEdit.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                });
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        // Redirect to the SellerLogin page
        Intent intent = new Intent(this, selection.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

}

