package com.example.arecanut;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BuyerProfile extends AppCompatActivity {

    private TextView cartTextView, myOrderTextView, addressTextView, ordersTextView,
            notificationsTextView, logoutTextView, personalDetailsTextView, nameTextView;

    private ImageView profileImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileactivity);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Assuming "USERNAME" is the user's unique identifier
        String email = getIntent().getStringExtra("email");
        Log.d("BuyerProfile", "Email received: " + email);

        // Initialize your TextViews and ImageView
        cartTextView = findViewById(R.id.carttxt);
        myOrderTextView = findViewById(R.id.myordertxt);
        addressTextView = findViewById(R.id.adresstxt);
        ordersTextView = findViewById(R.id.paymenttxt);
        notificationsTextView = findViewById(R.id.notificationtxt);
        logoutTextView = findViewById(R.id.logouttxt);
        personalDetailsTextView = findViewById(R.id.personeledittext);
        nameTextView = findViewById(R.id.txtname);
        profileImageView = findViewById(R.id.imageUser);

        // Fetch and display user information
        retrieveUserInfo(email);

        // Set click listeners for various TextViews
        setClickListeners(email);
    }


    private void retrieveUserInfo(String email) {
        Log.d("BuyerProfile", "Retrieving user info for email: " + email);
        // Replace invalid characters in email for Firebase Database path
        String sanitizedEmail = email.replace(".", "_");


        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(sanitizedEmail);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("BuyerProfile", "Snapshot exists: " + snapshot.exists());
                Log.d("BuyerProfile", "Snapshot key: " + snapshot.getKey());
                Log.d("BuyerProfile", "Snapshot value: " + snapshot.getValue());

                if (snapshot.exists()) {

                    // Check if 'name' and 'profileImageUrl' fields exist
                    if (snapshot.hasChild("name") || snapshot.hasChild("profileImageUrl")) {
                        // Retrieve 'name' and 'profileImageUrl'
                        String name = snapshot.child("name").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        // Update the nameTextView
                        nameTextView.setText(name);

                        // Load profile image using Picasso
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Picasso.get().load(profileImageUrl).into(profileImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // Handle successful image loading if needed
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Handle error if image loading fails
                                    e.printStackTrace();
                                }
                            });
                        }
                    } else {
                        Log.d("BuyerProfile", "Name or profileImageUrl is missing");
                        // Handle the case where 'name' or 'profileImageUrl' is missing
                        // You can add logging or display a toast message for debugging
                    }
                } else {
                    Log.d("BuyerProfile", "User data does not exist");
                    // Handle the case where the user data does not exist
                    // You can add logging or display a toast message for debugging
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BuyerProfile", "Database error: " + error.getMessage());
                // Handle database error
                // You can add logging or display a toast message for debugging
                error.toException();
            }
        });
    }



    private void setClickListeners(String email) {
        cartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click on cart TextView, navigate to the Cart activity
                String buyerEmail = getIntent().getStringExtra("email");
                Intent profileIntent = new Intent(BuyerProfile.this, BuyerCart.class);
                profileIntent.putExtra("email", buyerEmail);
                startActivity(profileIntent);
            }
        });

        myOrderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click on myOrder TextView, navigate to the MyOrder activity
                startActivity(new Intent(BuyerProfile.this, BuyerMyOrder.class));
            }
        });

        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click on address TextView, navigate to the Address activity
                startActivity(new Intent(BuyerProfile.this, BuyerAddress.class));
            }
        });

        ordersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the ShipUrl from Firebase under the ShippingId node
                DatabaseReference shipref = FirebaseDatabase.getInstance().getReference().child("ShippingId");
                shipref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            // Iterate over each child under ShippingId
                            String shipUrl = childSnapshot.child("ShipUrl").getValue(String.class);
                            if (shipUrl != null && !shipUrl.isEmpty()) {
                                // Open the URL in a browser
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shipUrl));
                                startActivity(intent);
                                return; // Stop further iteration after opening the first URL found
                            }
                        }
                        // If no valid ShipUrl found
                        Toast.makeText(BuyerProfile.this, "No ShipUrl found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(BuyerProfile.this, "Failed to retrieve data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        notificationsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click on notifications TextView, navigate to the Notification activity
                startActivity(new Intent(BuyerProfile.this, BuyerNotification.class));
            }
        });

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click on logout TextView, navigate to the selection activity
                startActivity(new Intent(BuyerProfile.this, selection.class));
            }
        });

        personalDetailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click on personal details TextView, navigate to EditProfile activity
                Intent editProfileIntent = new Intent(BuyerProfile.this, BuyerEditProfile.class);
                editProfileIntent.putExtra("email", email);
                startActivity(editProfileIntent);
            }
        });
    }
}
