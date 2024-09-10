package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BuyerDetails extends AppCompatActivity {
    MainModel mainModel;
    private TextView titleText, qualityText, quantityText, spinner2Text, editPriceText, priceText, descriptionText;
    private ImageView mainImage;
    private Button addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_details);

        titleText = findViewById(R.id.titletxt);
        qualityText = findViewById(R.id.qualitytxt);
        quantityText = findViewById(R.id.quantitytxt);
        spinner2Text = findViewById(R.id.spinner2txt);
        editPriceText = findViewById(R.id.editpricetxt);
        priceText = findViewById(R.id.pricetxt);
        descriptionText = findViewById(R.id.txtdescript);
        mainImage = findViewById(R.id.mainImage);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Get the productId from the Intent
        final String productId = getIntent().getStringExtra("productId");

        // Use productId to fetch data from Firebase
        DatabaseReference detailsRef = FirebaseDatabase.getInstance().getReference().child("Details").child(productId);
        detailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mainModel = dataSnapshot.getValue(MainModel.class);

                    // Populate the TextViews and ImageView with data
                    if (mainModel != null) {
                        titleText.setText(mainModel.getTitle());
                        qualityText.setText(mainModel.getCategory());
                        quantityText.setText(mainModel.getQuantity());
                        spinner2Text.setText(mainModel.getScale());
                        priceText.setText(mainModel.getPrice());
                        descriptionText.setText(mainModel.getDescription());

                        // Load image using Picasso or Glide
                        Picasso.get().load(mainModel.getImageUrl()).into(mainImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add the selected item to the cart
                retrieveEmailAndAddToCart(productId);

                // Optionally, you can redirect to the BuyerCart activity

            }
        });
    }

    // Inside BuyerDetails
    // Inside BuyerDetails
    private void retrieveEmailAndAddToCart(String productId) {
        DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference().child("Details").child(productId).child("email");
        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.getValue(String.class);

                    if (email != null) {
                        // Use the retrieved email to add the item to the cart
                        addToCart(email);
                    } else {
                        // Handle the case where email is null
                        Log.e("BuyerDetails", "Email is null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("BuyerDetails", "Firebase Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void addToCart(String email) {
        // Assuming you have a 'Cart' class with appropriate fields
        Cart cartItem = new Cart(
                mainModel.getProductId(),
                mainModel.getTitle(),
                mainModel.getCategory(),
                mainModel.getQuantity(),
                mainModel.getScale(),
                mainModel.getPrice(),
                mainModel.getImageUrl(),
                email // Add the userEmail to the Cart item
        );

        // Add the 'cartItem' to Firebase
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart");
        DatabaseReference newItemRef = cartRef.push();
        newItemRef.setValue(cartItem);

        // Update the totalPrice in each cart item
        updateTotalPrice(newItemRef.getKey());

        // Optionally, you can display a success message or update UI
        Toast.makeText(BuyerDetails.this, "Item added to cart", Toast.LENGTH_SHORT).show();

        // Redirect to the BuyerCart activity
        Intent intent = new Intent(BuyerDetails.this, BuyerCart.class);
        startActivity(intent);
    }



    // Add a method to update the total price in each cart item
    private void updateTotalPrice(String newItemKey) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String totalPrice = String.valueOf(0);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MainModel cartItem = snapshot.getValue(MainModel.class);
                    if (cartItem != null) {
                        // Check if the current cart item is the newly added item
                        if (cartItem.getProductId().equals(newItemKey)) {
                            totalPrice += cartItem.getTotal();
                        } else {
                            totalPrice += cartItem.getPrice(); // Assuming you have a 'getPrice()' method in MainModel
                        }
                    }
                }

                // Update the total price in the newly added item
                DatabaseReference newItemRef = cartRef.child(newItemKey);
                newItemRef.child("total").setValue(totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    // Add a method to get the total price from the cartList
    private double getTotalPrice(List<MainModel> cartList) {
        double totalPrice = 0;
        for (MainModel cartItem : cartList) {
            totalPrice += cartItem.getTotal();
        }
        return totalPrice;
    }


}
