package com.example.arecanut;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// SellerReview.java
// SellerReview.java
public class SellerReview extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SellerReviewCart reviewAdapter;
    private List<ReviewModel> reviewList;
    String sellerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellerreviews);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewList = new ArrayList<>();
        reviewAdapter = new SellerReviewCart(reviewList);
        recyclerView.setAdapter(reviewAdapter);
       sellerEmail = getIntent().getStringExtra("email");
        // Retrieve data from Firebase and update the adapter
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("MyOrders");

        // Add a listener to read the data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing data
                reviewList.clear();

                // Loop through each order in dataSnapshot
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderEmail = orderSnapshot.child("email").getValue(String.class);

                    // Check if the order's email matches the seller's email from the intent
                    if (sellerEmail != null && sellerEmail.equals(orderEmail)) {
                        // Extract specific fields
                        String category = orderSnapshot.child("category").getValue(String.class);
                        Long rating = orderSnapshot.child("rating").getValue(Long.class);
                        String review = orderSnapshot.child("review").getValue(String.class);
                        String imageUrl = orderSnapshot.child("imageUrl").getValue(String.class);

                        // Create a ReviewModel object and add it to the list
                        ReviewModel reviewItem = new ReviewModel(category, rating, review, imageUrl);
                        reviewList.add(reviewItem);
                    }
                }
                // Notify the adapter about the data change
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
