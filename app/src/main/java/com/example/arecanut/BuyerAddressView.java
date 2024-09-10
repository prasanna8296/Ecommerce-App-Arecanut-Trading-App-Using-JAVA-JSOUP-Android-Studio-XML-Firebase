package com.example.arecanut;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BuyerAddressView extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private EditText txtname, txtphoneno, txtaltphoneno, txtaddress, txtaddress1;
    private Button txtlocation, txtsaveaddress;

    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_address_adapter);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user_addresses");

        // Initialize UI elements
        txtname = findViewById(R.id.txtname);
        txtphoneno = findViewById(R.id.txtphoneno);
        txtaltphoneno = findViewById(R.id.txtaltphoneno);
        txtaddress = findViewById(R.id.txtaddress);
        txtaddress1 = findViewById(R.id.txtaddress1);

        txtlocation = findViewById(R.id.txtlocation);
        txtsaveaddress = findViewById(R.id.txtsaveaddress);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        txtlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLocationPermission()) {
                    getLastLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });

        txtsaveaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserDataToFirebase();
            }
        });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateAddressFields(location);
                        } else {
                            Toast.makeText(BuyerAddressView.this, "Location not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateAddressFields(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                String fullAddress = addresses.get(0).getAddressLine(0);

                // Find the midpoint based on word boundaries
                int midpoint = findMidpoint(fullAddress);

                // Split the address into two parts
                String firstHalf = fullAddress.substring(0, midpoint).trim();
                String secondHalf = fullAddress.substring(midpoint).trim();

                // Update the address fields
                txtaddress.setText(firstHalf);
                txtaddress1.setText(secondHalf);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(BuyerAddressView.this, "Error getting address", Toast.LENGTH_SHORT).show();
        }
    }

    // Find the midpoint based on word boundaries
    private int findMidpoint(String text) {
        int length = text.length();
        int midpoint = length / 2;

        // Find the closest space to the midpoint
        for (int i = midpoint; i < length; i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }

        for (int i = midpoint; i >= 0; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }

        // If no space is found, return the original midpoint
        return midpoint;
    }



    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserDataToFirebase() {
        String name = txtname.getText().toString().trim();
        String phoneNo = txtphoneno.getText().toString().trim();
        String altPhoneNo = txtaltphoneno.getText().toString().trim();
        String address = txtaddress.getText().toString().trim();
        String address1 = txtaddress1.getText().toString().trim();

        if (validateInput(name, phoneNo, altPhoneNo, address)) {
            User user = new User(name, phoneNo, altPhoneNo, address, address1);
            String userId = databaseReference.push().getKey();
            databaseReference.child(userId).setValue(user);

            Toast.makeText(BuyerAddressView.this, "Details Updated successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(BuyerAddressView.this, BuyerAddress.class);
            startActivity(intent);
        }
    }

    private boolean validateInput(String name, String phoneNo, String altPhoneNo, String address) {
        if (name.isEmpty() || phoneNo.isEmpty() || address.isEmpty()) {
            Toast.makeText(BuyerAddressView.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPhoneNumber(phoneNo)) {
            Toast.makeText(BuyerAddressView.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!altPhoneNo.isEmpty() && !isValidPhoneNumber(altPhoneNo)) {
            Toast.makeText(BuyerAddressView.this, "Invalid Alternate Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNo.equals(altPhoneNo)) {
            Toast.makeText(BuyerAddressView.this, "Alternate Phone Number should be different from the Primary Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        }

            return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    private static class User {
        public String name;
        public String phoneNo;
        public String altPhoneNo;
        public String address;
        public String address1;

        public User() {
            // Default constructor required for DataSnapshot.getValue(User.class)
        }

        public User(String name, String phoneNo, String altPhoneNo, String address, String address1) {
            this.name = name;
            this.phoneNo = phoneNo;
            this.altPhoneNo = altPhoneNo;
            this.address = address;
            this.address1 = address1;
        }
    }
}
