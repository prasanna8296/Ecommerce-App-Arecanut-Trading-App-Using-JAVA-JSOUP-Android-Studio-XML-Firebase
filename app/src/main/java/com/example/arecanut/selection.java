package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class selection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection);

        // Initialize buttons
        Button sellerButton = findViewById(R.id.button);
        Button buyerButton = findViewById(R.id.button2);

        // Set onClickListener for Buyer button
        buyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to B uyerActivity and pass the value "buyer"
                Intent intent = new Intent(selection.this, LoginActivity.class);
                intent.putExtra("userType", "buyer");
                startActivity(intent);
            }
        });

        // Set onClickListener for Seller button
        sellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SellerActivity and pass the value "seller"
                Intent intent = new Intent(selection.this, LoginActivity.class);
                intent.putExtra("userType", "seller");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Override the back button behavior after login
        moveTaskToBack(true);
    }
}
