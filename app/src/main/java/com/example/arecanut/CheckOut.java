package com.example.arecanut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckOut extends AppCompatActivity implements PaymentResultListener {

    private TextView deliveryDetailsTextView;
    private ImageView locationImageView;
    private TextView totalPriceTextView;
    private TextView additionalChargeTextView;
    private TextView finalPriceTextView;
    private Button pay; // Declare the pay button at the class level
    private Button placeOrderButton; // Declare the place order button at the class level

    private boolean isPaymentSuccessful = false; // Flag to check if payment is successful
    private boolean isAddressProvided = false; // Flag to check if address is provided

    String CustomerId;
    String EphericalKey;
    String ClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        deliveryDetailsTextView = findViewById(R.id.txtdeliverydetails);
        locationImageView = findViewById(R.id.txtlocationview);
        totalPriceTextView = findViewById(R.id.pricetxt1);
        additionalChargeTextView = findViewById(R.id.pricetxt2);
        finalPriceTextView = findViewById(R.id.pricetxt3);
        placeOrderButton = findViewById(R.id.payNow); // Initialize the place order button
        pay = findViewById(R.id.paybutton); // Initialize the pay button

        Checkout.preload(CheckOut.this);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double finalPrice = Double.parseDouble(finalPriceTextView.getText().toString());
                startPayment((int) finalPrice);
            }
        });

        // Retrieve the total price value from the intent
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);
        totalPriceTextView.setText(String.format("%.2f", totalPrice));

        // Calculate additional charge and update the corresponding TextView
        double additionalCharge = 100.0;
        additionalChargeTextView.setText(String.format("%.2f", additionalCharge));

        // Calculate the final price by adding the total price and additional charge
        double finalPrice = totalPrice + additionalCharge;
        finalPriceTextView.setText(String.format("%.2f", finalPrice));

        deliveryDetailsTextView = findViewById(R.id.txtdeliverydetails);
        locationImageView = findViewById(R.id.txtlocationview);

        // Retrieve the selected user address from BuyerAddress activity
        Map<String, Object> selectedUserAddress = (Map<String, Object>) getIntent().getSerializableExtra("selectedUserAddress");

        if (selectedUserAddress != null) {
            updateDeliveryDetails(selectedUserAddress);
        } else {
            deliveryDetailsTextView.setText("No delivery details available");
        }

        locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOut.this, BuyerAddress.class);
                startActivityForResult(intent, 1);
            }
        });

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAddressProvided) {
                    Toast.makeText(CheckOut.this, "Please provide a delivery address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPaymentSuccessful) {
                    Toast.makeText(CheckOut.this, "Please complete the payment first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedAddress = deliveryDetailsTextView.getText().toString();
                double totalPrice = Double.parseDouble(totalPriceTextView.getText().toString());
                Order order = new Order(selectedAddress, finalPrice, selectedUserAddress);
                saveOrderToFirebase(order);
                String buyerEmail = getIntent().getStringExtra("email");
                if (buyerEmail != null) {
                    Log.d("BuyerHome", "Email received on se create: " + buyerEmail);
                } else {
                    Log.e("BuyerHome", "Email is null on create");
                }

                Intent profileIntent = new Intent(CheckOut.this, BuyerHome.class);
                profileIntent.putExtra("email", buyerEmail);
                startActivity(profileIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Map<String, Object> selectedAddress = (Map<String, Object>) data.getSerializableExtra("selectedUserAddress");
            updateDeliveryDetails(selectedAddress);
        }
    }

    private void updateDeliveryDetails(Map<String, Object> userAddress) {
        if (userAddress != null) {
            String txtAddress = userAddress.get("address").toString();
            String txtAddress1 = userAddress.get("address1").toString();
            String fullAddress = txtAddress + " " + txtAddress1;
            deliveryDetailsTextView.setText(fullAddress);
            isAddressProvided = true; // Set address provided flag to true
        } else {
            deliveryDetailsTextView.setText("No delivery details available");
            isAddressProvided = false; // Set address provided flag to false
        }
    }

    private void saveOrderToFirebase(Order order) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("MyOrders");

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String orderId = snapshot.getKey();
                    DatabaseReference orderItemRef = ordersRef.child(orderId);
                    orderItemRef.child("selectedAddress").setValue(order.getSelectedAddress());
                    orderItemRef.child("finalPrice").setValue(order.getFinalPrice());
                }

                Intent intent = new Intent(CheckOut.this, BuyerHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public void startPayment(int Amount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_UkwvRytQeRByd3");

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "RazorPay Demo");
            jsonObject.put("description", "If");
            jsonObject.put("theme.color", "#3399cc");
            jsonObject.put("currency", "INR");
            jsonObject.put("amount", Amount * 100);

            JSONObject retryobject = new JSONObject();
            retryobject.put("enabled", true);
            retryobject.put("max_count", 4);

            jsonObject.put("retry", retryobject);
            checkout.open(CheckOut.this, jsonObject);
        } catch (Exception e) {
            Toast.makeText(CheckOut.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(CheckOut.this, "Payment Success", Toast.LENGTH_SHORT).show();
        pay.setEnabled(false); // Disable the pay button on successful payment
        isPaymentSuccessful = true; // Set payment successful flag to true
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(CheckOut.this, "Payment Failure", Toast.LENGTH_SHORT).show();
        isPaymentSuccessful = false; // Set payment successful flag to false
    }
}
