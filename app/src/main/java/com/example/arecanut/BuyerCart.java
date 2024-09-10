package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class BuyerCart extends AppCompatActivity implements CartAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<MainModel> cartItemList;
    private TextView totalPriceTextView;
    private Button proceedToCheckoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_cart);

        recyclerView = findViewById(R.id.recyclerView);
        totalPriceTextView = findViewById(R.id.pricetxt);
        proceedToCheckoutButton = findViewById(R.id.proceedToCheckOut);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItemList);
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.setOnItemClickListener(this);

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartItemList.clear();
                double totalCartPrice = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MainModel cartItem = snapshot.getValue(MainModel.class);
                    cartItemList.add(cartItem);

                    // Calculate total price for the current item and add to the totalCartPrice
                    totalCartPrice += cartItem.getTotal();
                }

                // Update the total price text view
                updateTotalPrice(totalCartPrice);

                // Notify the adapter about the data change
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        proceedToCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save each item to Firebase MyOrders
                saveOrderToFirebase(cartItemList);

                String buyerEmail = getIntent().getStringExtra("email");
                // Pass the total price to the CheckOut activity
                Intent intent = new Intent(BuyerCart.this, CheckOut.class);
                intent.putExtra("totalPrice", getTotalCartPrice());
                intent.putExtra("email", buyerEmail);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDeleteClick(int position) {
        // Handle delete click here
        if (position >= 0 && position < cartItemList.size()) {
            cartItemList.remove(position); // Remove item from list
            cartAdapter.notifyItemRemoved(position); // Notify adapter of removal
            updateTotalPrice(getTotalCartPrice()); // Update total price after removal
        }
    }

    @Override
    public void onTotalPriceChange(double totalPrice) {
        // Update the total price text view
        updateTotalPrice(totalPrice);
    }

    private void updateTotalPrice(double totalPrice) {
        totalPriceTextView.setText(String.format("Total: %.2f", totalPrice));
    }

    private void saveOrderToFirebase(List<MainModel> cartItems) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("MyOrders");

        for (MainModel cartItem : cartItems) {
            String orderId = ordersRef.push().getKey();
            DatabaseReference orderItemRef = ordersRef.child(orderId);

            // Copy other fields
            orderItemRef.child("title").setValue(cartItem.getTitle());
            orderItemRef.child("category").setValue(cartItem.getCategory());
            orderItemRef.child("quantity").setValue(cartItem.getQuantity());
            orderItemRef.child("scale").setValue(cartItem.getScale());
            orderItemRef.child("price").setValue(cartItem.getPrice());
            orderItemRef.child("imageUrl").setValue(cartItem.getImageUrl());
            orderItemRef.child("email").setValue(cartItem.getEmail());
            // Add total price as a separate field
            orderItemRef.child("totalPrice").setValue(getTotalCartPrice());
        }
    }

    private double getTotalCartPrice() {
        double totalCartPrice = 0;

        for (MainModel cartItem : cartItemList) {
            totalCartPrice += cartItem.getTotal();
        }

        return totalCartPrice;
    }
}
