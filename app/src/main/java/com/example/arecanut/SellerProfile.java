package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerProfile extends AppCompatActivity {
    private static final String TAG = "SellerProfile";
    private DatabaseReference databaseReference;
    private TextView nameTextView, emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellerprofile);
        String sellerEmail = getIntent().getStringExtra("email");

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("sellers");

        // Find TextViews
        nameTextView = findViewById(R.id.textView);
        emailTextView = findViewById(R.id.textView2);

        // Retrieve and display seller details
        retrieveSellerDetails();
    }

    private void retrieveSellerDetails() {
        // Retrieve the seller's email from the intent
        String sellerEmail = getIntent().getStringExtra("email");

        if (sellerEmail != null && !sellerEmail.isEmpty()) {
            databaseReference.orderByChild("email").equalTo(sellerEmail)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Assuming there's only one matching seller
                                DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                Seller seller = firstChild.getValue(Seller.class);

                                if (seller != null) {
                                    // Set name and email to TextViews
                                    nameTextView.setText(seller.getName());
                                    emailTextView.setText(seller.getEmail());

                                    // Load profile picture
                                    loadProfilePicture(seller.getProfilePictureUrl());
                                } else {
                                    Log.e(TAG, "Seller object is null");
                                }
                            } else {
                                // Handle the case where the email is not found in the database
                                Log.d(TAG, "DataSnapshot does not exist for email: " + sellerEmail);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "Error: " + databaseError.getMessage());
                            // Handle error
                        }
                    });
        } else {
            Log.e(TAG, "Seller email is null or empty");
        }

    // Method to load the profile picture using Glide or your preferred image loading library

        Button myreview = findViewById(R.id.button);
        Button myproduct = findViewById(R.id.button2);
        Button personalinfo = findViewById(R.id.button3);
//        Button notify = findViewById(R.id.button4);
        Button manage = findViewById(R.id.button5);
        Button backpage = findViewById(R.id.buttonback);

        myproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sellerEmail = getIntent().getStringExtra("email");
                Intent myintent = new Intent(SellerProfile.this, ManageOrder.class);
                myintent.putExtra("email",sellerEmail);
                startActivity(myintent);
            }
        });
        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sellerEmail = getIntent().getStringExtra("email");
                Intent myintent = new Intent(SellerProfile.this, ManageSeller.class);
                myintent.putExtra("email",sellerEmail);
                startActivity(myintent);
            }
        });
//        notify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SellerProfile.this, SellerNotification.class);
//                startActivity(intent);
//            }
//        });
        myreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sellerEmail = getIntent().getStringExtra("email");
                Intent myintent = new Intent(SellerProfile.this, SellerReview.class);
                myintent.putExtra("email",sellerEmail);
                startActivity(myintent);
            }
        });
        personalinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sellerEmail = getIntent().getStringExtra("email");
                Intent editProfileIntent = new Intent(SellerProfile.this, SellerProfileEdit.class);
                editProfileIntent.putExtra("email", sellerEmail);
                startActivity(editProfileIntent);
            }
        });
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProfile.this, MainActivity.class);
                finish();
            }
        });
    }

    // Method to load the profile picture using Glide or your preferred image loading library
    private void loadProfilePicture(String profilePictureUrl) {
        ImageView profileImageView = findViewById(R.id.imageView2);

        // Use Glide to load the profile picture into the ImageView
        Glide.with(this)
                .load(profilePictureUrl)
                // Placeholder image while loading// Image to show in case of error
                .into(profileImageView);
    }
}
