package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BuyerAddress extends AppCompatActivity implements BuyerAddressCart.OnEditClickListener, BuyerAddressCart.OnDeleteClickListener, BuyerAddressCart.OnItemClickListener {
    private RecyclerView recyclerView;
    private BuyerAddressCart adapter;
    private List<UserAddress> userAddresses;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_address);

        recyclerView = findViewById(R.id.recyclerView2); // Replace with your RecyclerView ID
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAddresses = new ArrayList<>();
        adapter = new BuyerAddressCart(userAddresses, this, this, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user_addresses");

        // Fetch data from Firebase
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userAddresses.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserAddress userAddress = snapshot.getValue(UserAddress.class);
                    if (userAddress != null) {
                        userAddresses.add(userAddress);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BuyerAddress.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        TextView txtnewaddress = findViewById(R.id.txtnewaddress);

        txtnewaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to the NewAddressActivity
                Intent intent = new Intent(BuyerAddress.this, BuyerAddressView.class);
                startActivity(intent);
            }
        });
    }

    // Implement the OnEditClickListener interface
    @Override
    public void onEditClick(int position) {
        // Handle the edit button click
        // Redirect to the BuyerAddressView with the selected user data
        UserAddress selectedUser = userAddresses.get(position);
        Intent intent = new Intent(BuyerAddress.this, BuyerAddressView.class);
        // Pass the selected user data to the next activity using intent
        intent.putExtra("selectedUser", selectedUser);
        startActivity(intent);
    }

    // Implement the OnDeleteClickListener interface
    @Override
    public void onDeleteClick(final int position) {
        // Handle the delete button click

        // Remove the item from the list
        UserAddress deletedUser = userAddresses.remove(position);

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();

        // Remove the item from Firebase
        if (deletedUser != null) {
            String userId = deletedUser.getId();
            if (userId != null) {
                DatabaseReference itemReference = databaseReference.child(userId);
                itemReference.removeValue();
            }
        }
    }

    // Implement the OnItemClickListener interface
    @Override
    public void onItemClick(int position) {
        // Get the selected user address
        UserAddress selectedUserAddress = userAddresses.get(position);

        // Create an Intent to send the selected user address back to CheckOut activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedUserAddress", (Serializable) selectedUserAddress.toMap());
        // Assuming UserAddress has a toMap() method

        // Set the result and finish the activity
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
