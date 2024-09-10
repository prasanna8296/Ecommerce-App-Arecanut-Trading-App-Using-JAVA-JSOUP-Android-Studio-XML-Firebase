package com.example.arecanut;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BuyerMyorderView extends AppCompatActivity {

    private TextView categoryTextView;
    private TextView sp1TextView;
    private TextView priceTextView;
    private ImageView imageView;
    private TextView quantityTextView;
    private TextView sp2TextView;
    private TextView address;
    private TextView txtReview;
    private RatingBar ratingBar;
    private Button submitButton;

    private OrderModel order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_myorderview);

        categoryTextView = findViewById(R.id.categorytext);
        sp1TextView = findViewById(R.id.qualitytext);
        quantityTextView = findViewById(R.id.quantitytext);
        sp2TextView = findViewById(R.id.spinner2txt);
        priceTextView = findViewById(R.id.pricetext);
        imageView = findViewById(R.id.img1);
        address = findViewById(R.id.txtdeliverydetails);
        txtReview = findViewById(R.id.txtreview);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitbutton);

        order = (OrderModel) getIntent().getSerializableExtra("order");

        if (order != null) {
            categoryTextView.setText(order.getTitle());
            sp1TextView.setText(order.getCategory());
            quantityTextView.setText(order.getQuantity());
            sp2TextView.setText(order.getScale());
            priceTextView.setText(order.getPrice());
            address.setText(order.getSelectedAddress());
            Picasso.get().load(order.getImageUrl()).into(imageView);
            // Display other fields as needed
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve order details
                String review = txtReview.getText().toString();
                float rating = ratingBar.getRating();

                // Get the price from the OrderModel
                String price = order.getPrice();

                // Update Firebase
                updateFirebase(price, review, rating);

            }
        });
    }

    private void updateFirebase(String price, String review, float rating) {
        // Perform the update in Firebase based on the price
        // ...

        // For example, if you have a "MyOrders" node in Firebase
        DatabaseReference myOrdersRef = FirebaseDatabase.getInstance().getReference().child("MyOrders");

        // Assuming you have a unique key for each order, you can query and update the specific order
        myOrdersRef.orderByChild("price").equalTo(price).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Update the review and rating for the matched order
                    snapshot.getRef().child("review").setValue(review);
                    snapshot.getRef().child("rating").setValue(rating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}

