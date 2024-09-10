package com.example.arecanut;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShippingId extends AppCompatActivity {
    TextView ShipId;
    Button Addbutton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ship_address);

        ShipId = findViewById(R.id.shipid);
        Addbutton = findViewById(R.id.add_button);

        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shipUrl = ShipId.getText().toString().trim();

                if (!shipUrl.isEmpty()) {
                    // Save the ShipUrl to Firebase
                    saveShipUrlToFirebase(shipUrl);
                } else {
                    Toast.makeText(ShippingId.this, "Please enter Shipping URL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveShipUrlToFirebase(String shipUrl) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ShippingId");

        // Get the order ID from Intent
        String orderId = getIntent().getStringExtra("orderId");

        // Check if orderId is not null and not empty
        if (orderId != null && !orderId.isEmpty()) {
            // Update ShipUrl field in the Firebase database under the specific order ID
            databaseReference.child(orderId).child("ShipUrl").setValue(shipUrl, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error == null) {
                        Toast.makeText(ShippingId.this, "Shipping URL added successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity after updating the ShipUrl
                    } else {
                        Toast.makeText(ShippingId.this, "Failed to add Shipping URL", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(ShippingId.this, "Order ID is invalid", Toast.LENGTH_SHORT).show();
        }
    }
}
