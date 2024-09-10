package com.example.arecanut;

import android.content.Intent;
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

public class ManageSeller extends AppCompatActivity implements ManageCartAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ManageCartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private String sellerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manageseller);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItemList = new ArrayList<>();
        cartAdapter = new ManageCartAdapter(cartItemList, this);
        recyclerView.setAdapter(cartAdapter);
        sellerEmail = getIntent().getStringExtra("email");

        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("MyOrders");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey(); // Get the orderId
                    String orderEmail = orderSnapshot.child("email").getValue(String.class);

                    if (sellerEmail != null && sellerEmail.equals(orderEmail)) {
                        String title = orderSnapshot.child("title").getValue(String.class);
                        String category = orderSnapshot.child("category").getValue(String.class);
                        String quantity = orderSnapshot.child("quantity").getValue(String.class);
                        String scale = orderSnapshot.child("scale").getValue(String.class);
                        String price = orderSnapshot.child("price").getValue(String.class);
                        String imageUrl = orderSnapshot.child("imageUrl").getValue(String.class);
                        String selectedAddress = orderSnapshot.child("selectedAddress").getValue(String.class);

                        if (selectedAddress != null) {
                            CartItem cartItem = new CartItem(orderId, title, category, quantity, scale, price, imageUrl, selectedAddress);
                            cartItemList.add(cartItem);
                        }
                    }
                }

                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    @Override
    public void onItemClick(String orderId) {
        Intent intent = new Intent(ManageSeller.this, ShippingId.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }
}
