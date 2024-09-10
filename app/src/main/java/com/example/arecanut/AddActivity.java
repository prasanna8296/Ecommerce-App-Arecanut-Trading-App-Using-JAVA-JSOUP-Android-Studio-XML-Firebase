package com.example.arecanut;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.FirebaseDatabase;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    TextView category;
    TextView quantity;
    TextView price;
    TextView description;
    Spinner spinner1, spinner2, spinner3, spinner4;
    ImageView imageView;
    Button btnadd, btnback, prediction;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Details");
    private StorageReference storageReference;
    private Uri imageUri;

    private ArrayAdapter<String> spinner1Adapter, spinner2Adapter;
    private List<String> spinner1Items, spinner2Items;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        category = findViewById(R.id.titleview);
        quantity = findViewById(R.id.textquantity);
        price = findViewById(R.id.pricetextview);
        description = findViewById(R.id.textViewdescription);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        imageView = findViewById(R.id.imageViewadd);
        btnadd = findViewById(R.id.productaddbutton);
        btnback = findViewById(R.id.backbutton);
        prediction = findViewById(R.id.prediction);
        spinner3 = findViewById(R.id.spinnerCategory);
        spinner4 = findViewById(R.id.spinnerMonth);

        storageReference = FirebaseStorage.getInstance().getReference();
        spinner1Items = Arrays.asList("Rashi", "api", "Bette", "Kempugotu", "Chali", "Cqca", "Bilegotu", "Tattibettee", "New Variety", "Ripe");
        spinner2Items = Arrays.asList("Gram", "Kg", "Ton");

        spinner1Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner1Items);
        spinner2Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner2Items);

        spinner1.setAdapter(spinner1Adapter);
        spinner2.setAdapter(spinner2Adapter);

        List<String> spinner3Items = Arrays.asList("Rashi", "api", "Bette", "Kempugotu", "Chali", "Cqca", "Bilegotu", "Tattibettee", "New Variety", "Ripe");
        ArrayAdapter<String> spinner3Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinner3Items);
        spinner3.setAdapter(spinner3Adapter);

        // Set today's date directly in the spinner4
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
        ArrayAdapter<String> spinner4Adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(currentDate));
        spinner4.setAdapter(spinner4Adapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add logic to select an image from the device
                // You can use an Intent to open the image picker
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        prediction.setOnClickListener(v -> {
            String selectedCategory = spinner3.getSelectedItem().toString();
            String selectedDate = spinner4.getSelectedItem().toString();

            PredictionManager.predictOptimumPrice(selectedCategory, selectedDate, new PredictionManager.PredictionCallback() {
                @Override
                public void onPredictionReceived(double predictedPrice) {
                    // Use the predicted price here
                    price.setText(String.valueOf(predictedPrice));
                }
            });
        });

        btnadd.setOnClickListener(v -> {
            insertData();
        });
        btnback.setOnClickListener(v -> finish());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri muri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }

    private void insertData() {
        String titleValue = category.getText().toString().trim();
        String quantityValue = quantity.getText().toString().trim();
        String priceValue = price.getText().toString().trim();
        String descriptionValue = description.getText().toString().trim();
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("email");

        if (TextUtils.isEmpty(titleValue) || TextUtils.isEmpty(quantityValue) || TextUtils.isEmpty(priceValue) || TextUtils.isEmpty(descriptionValue)) {
            Toast.makeText(AddActivity.this, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantityNumber;
        try {
            quantityNumber = Integer.parseInt(quantityValue);
        } catch (NumberFormatException e) {
            Toast.makeText(AddActivity.this, "Invalid quantity format.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedUnit = spinner2.getSelectedItem().toString();

        if (("Ton".equals(selectedUnit) && quantityNumber > 1000)
                || ("Kg".equals(selectedUnit) && quantityNumber > 1000)
                || ("Gram".equals(selectedUnit) && quantityNumber > 1000)) {
            Toast.makeText(AddActivity.this, "Quantity cannot exceed 1000 for the selected unit.", Toast.LENGTH_SHORT).show();
            return;
        }
        double priceDouble;
        try {
            priceDouble = Double.parseDouble(priceValue);
        } catch (NumberFormatException e) {
            Toast.makeText(AddActivity.this, "Invalid price format.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceDouble > 1000000) {
            Toast.makeText(AddActivity.this, "Price cannot exceed 10 lakh.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference imageRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String modelId = root.push().getKey();

                            Map<String, Object> map = new HashMap<>();
                            map.put("email", userEmail); // Save email
                            map.put("imageUrl", uri.toString());
                            map.put("title", category.getText().toString());
                            map.put("quantity", quantity.getText().toString());
                            map.put("price", price.getText().toString());
                            map.put("description", description.getText().toString());
                            map.put("category", spinner1.getSelectedItem().toString());
                            map.put("scale", spinner2.getSelectedItem().toString());

                            // Retrieve address from Firebase sellers based on the user's email
                            DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference("sellers");
                            sellersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            // Assuming the address is stored under the "address" field in Firebase sellers
                                            String address = dataSnapshot.child("address").getValue(String.class);
                                            map.put("address", address);

                                            root.child(modelId)
                                                    .setValue(map)
                                                    .addOnSuccessListener(unused -> {
                                                        Toast.makeText(AddActivity.this, "Data Inserted Successfully.", Toast.LENGTH_SHORT).show();
                                                        clearAll();
                                                        String sellerEmail = getIntent().getStringExtra("email");
                                                        Intent myintent = new Intent(AddActivity.this, MainActivity.class);
                                                        myintent.putExtra("email",sellerEmail);

                                                        startActivity(myintent);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(AddActivity.this, "Error while insertion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.e("FirebaseDatabase", "Error during data insertion", e);
                                                    });
                                        }
                                    } else {
                                        Log.d("AddActivity", "No seller found with email: " + userEmail);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("AddActivity", "Error retrieving seller data: " + error.getMessage());
                                }
                            });

                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseStorage", "Error uploading image", e);
                        Toast.makeText(AddActivity.this, "Error uploading image.", Toast.LENGTH_SHORT).show();
                    });

        } else {
            Toast.makeText(AddActivity.this, "Please select an image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAll() {
        category.setText("");
        quantity.setText("");
        price.setText("");
        description.setText("");
        imageView.setImageDrawable(null);
    }
}
