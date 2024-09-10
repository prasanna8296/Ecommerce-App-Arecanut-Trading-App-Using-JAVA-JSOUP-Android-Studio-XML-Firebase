package com.example.arecanut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SellerDetails extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, adharEditText, panEditText, addressEditText;
    private Button signupButton;
    private TextView loginRedirectText;
    private ImageView locationIcon;

    private DatabaseReference databaseReference;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sellerdetails);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("sellers");

        // Initialize UI elements
        nameEditText = findViewById(R.id.textView);
        emailEditText = findViewById(R.id.textView2);
        phoneEditText = findViewById(R.id.textView3);
        passwordEditText = findViewById(R.id.textpassword);
        adharEditText = findViewById(R.id.textView4);
        panEditText = findViewById(R.id.textView5);
        addressEditText = findViewById(R.id.textView6);
        locationIcon = findViewById(R.id.locationIcon);

        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Set onClickListener for the signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate input fields
                if (validateInput()) {
                    // If validation passes, proceed with registration
                    try {
                        // Get user input
                        final String email = emailEditText.getText().toString().trim();
                        final String phone = phoneEditText.getText().toString().trim();
                        final String adhar = adharEditText.getText().toString().trim();
                        final String pan = panEditText.getText().toString().trim();

                        // Check uniqueness of email
                        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // Email is unique, proceed with other checks
                                    // Check uniqueness of phone
                                    databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (!dataSnapshot.exists()) {
                                                // Phone is unique, proceed with other checks
                                                // Check uniqueness of adhar
                                                databaseReference.orderByChild("adhar").equalTo(adhar).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (!dataSnapshot.exists()) {
                                                            // Aadhar is unique, proceed with other checks
                                                            // Check uniqueness of PAN
                                                            databaseReference.orderByChild("pan").equalTo(pan).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (!dataSnapshot.exists()) {
                                                                        // PAN is unique, proceed with registration
                                                                        String name = nameEditText.getText().toString().trim();
                                                                        String phone = phoneEditText.getText().toString().trim();
                                                                        String password = passwordEditText.getText().toString().trim();
                                                                        String adhar = adharEditText.getText().toString().trim();
                                                                        String pan = panEditText.getText().toString().trim();
                                                                        String address = addressEditText.getText().toString().trim();

                                                                        // Create a Seller object with the user input
                                                                        Seller seller = new Seller(name, email, phone, password, adhar, pan, address);

                                                                        // Push the seller object to Firebase
                                                                        String key = databaseReference.push().getKey();
                                                                        databaseReference.child(key).setValue(seller);

                                                                        // Display a success message or perform any additional actions
                                                                        Toast.makeText(SellerDetails.this, "Seller registered successfully", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(SellerDetails.this, MainActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    } else {
                                                                        // PAN already exists
                                                                        Toast.makeText(SellerDetails.this, "PAN already registered", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    // Handle error
                                                                    Toast.makeText(SellerDetails.this, "Error checking PAN uniqueness", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        } else {
                                                            // Aadhar already exists
                                                            Toast.makeText(SellerDetails.this, "Aadhar already registered", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Handle error
                                                        Toast.makeText(SellerDetails.this, "Error checking Aadhar uniqueness", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                // Phone already exists
                                                Toast.makeText(SellerDetails.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle error
                                            Toast.makeText(SellerDetails.this, "Error checking phone number uniqueness", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    // Email already exists
                                    Toast.makeText(SellerDetails.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error
                                Toast.makeText(SellerDetails.this, "Error checking email uniqueness", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SellerDetails.this, "Error registering seller", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Set onClickListener for the login redirect text
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(SellerDetails.this, SellerLogin.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SellerDetails.this, "Error redirecting to login page", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for the location icon
        locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for location permission before requesting updates
                if (ContextCompat.checkSelfPermission(SellerDetails.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Start location updates
                    updateLocation();
                } else {
                    // Request location permission if not granted
                    ActivityCompat.requestPermissions(SellerDetails.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }
        });

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    getAddressFromLocation(latitude, longitude);
                    // Stop location updates after getting the address
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        };
    }

    private void updateLocation() {
        // Check for location permission before requesting updates
        if (ContextCompat.checkSelfPermission(SellerDetails.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request last known location
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location location = task.getResult();
                                long timeDifference = System.currentTimeMillis() - location.getTime();
                                // Check if the location is recent (within the last 5 minutes, for example)
                                if (timeDifference < 5 * 60 * 1000) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    getAddressFromLocation(latitude, longitude);
                                } else {
                                    // If the location is not recent, request location updates
                                    requestLocationUpdates();
                                }
                            } else {
                                // If last known location is not available, request location updates
                                requestLocationUpdates();
                            }
                        }
                    });
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(SellerDetails.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    // Helper method to request location updates
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
    }

    // Helper method to create a location request
    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // Update location every 10 seconds
                .setFastestInterval(5000); // The fastest update interval, in milliseconds
    }

    // Helper method to get address from latitude and longitude
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Get the address line and update the EditText
                String addressText = address.getAddressLine(0);
                addressEditText.setText(addressText);
            } else {
                Toast.makeText(SellerDetails.this, "Unable to get address", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(SellerDetails.this, "Error getting address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Validate input fields
    private boolean validateInput() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String adhar = adharEditText.getText().toString().trim();
        String pan = panEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // Check if any field is empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(adhar) || TextUtils.isEmpty(pan) ||
                TextUtils.isEmpty(address)) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the email is valid
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the phone number is valid
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if the password is valid
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Invalid password (must be at least 6 characters)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidAadhar(adhar)) {
            Toast.makeText(this, "Invalid Aadhar number", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if PAN card number is valid
        if (!isValidPan(pan)) {
            Toast.makeText(this, "Invalid PAN card number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Phone number validation method
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    // Password validation method
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isValidAadhar(String adhar) {
        return adhar.length() == 12 && TextUtils.isDigitsOnly(adhar);
    }

    // PAN card validation method
    private boolean isValidPan(String pan) {
        return pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    }
}
