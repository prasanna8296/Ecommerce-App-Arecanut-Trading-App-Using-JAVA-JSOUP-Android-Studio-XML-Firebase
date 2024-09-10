package com.example.arecanut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuyerHome extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private static final String PREFS_NAME = "BuyerHomePrefs";
    private static final String KEY_EMAIL = "buyerEmail";

    private ProductAdapter mainProductAdapter;
    RecyclerView recyclerView;
    private String buyerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_home);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the user's email from the intent or SharedPreferences
        buyerEmail = getIntent().getStringExtra("email");
        if (buyerEmail == null) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            buyerEmail = prefs.getString(KEY_EMAIL, null);  // No default email
            if (buyerEmail == null) {
                Log.e("BuyerHome", "Email is null and no saved email found. Redirecting to login.");
                redirectToLogin();
                return;
            } else {
                Log.d("BuyerHome", "Email retrieved from SharedPreferences: " + buyerEmail);
            }
        } else {
            // Save the email to SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(KEY_EMAIL, buyerEmail);
            editor.apply();
            Log.d("BuyerHome", "Email received on create: " + buyerEmail);
        }

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Details"), MainModel.class)
                        .build();

        mainProductAdapter = new ProductAdapter(this, options, this);
        recyclerView.setAdapter(mainProductAdapter);

        FloatingActionButton fab1 = findViewById(R.id.homebutton);
        FloatingActionButton fab2 = findViewById(R.id.notificationbutton);
        FloatingActionButton fab3 = findViewById(R.id.cartbutton);
        FloatingActionButton fab4 = findViewById(R.id.profilebutton);

        // Example click listener for FAB1
        fab1.setOnClickListener(view -> {
            Intent intent = new Intent(BuyerHome.this, BuyerHome.class);
            intent.putExtra("email", buyerEmail);
            startActivity(intent);
        });

        fab2.setOnClickListener(view -> {
            Intent intent = new Intent(BuyerHome.this, BuyerNotification.class);
            intent.putExtra("email", buyerEmail);
            startActivity(intent);
        });

        fab3.setOnClickListener(view -> {
            Intent intent = new Intent(BuyerHome.this, BuyerCart.class);
            intent.putExtra("email", buyerEmail);
            startActivity(intent);
        });

        fab4.setOnClickListener(view -> {
            Intent profileIntent = new Intent(BuyerHome.this, BuyerProfile.class);
            profileIntent.putExtra("email", buyerEmail);
            startActivity(profileIntent);
        });

        SearchView searchView = findViewById(R.id.searchbox);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return false;
            }
        });
    }

    private void searchProducts(String query) {
        Log.d("BuyerHome", "searchProducts called with query: " + query);
        Query searchQuery = FirebaseDatabase.getInstance().getReference().child("Details")
                .orderByChild("category")
                .startAt(query.toUpperCase())
                .endAt(query.toLowerCase() + "\uf8ff");

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("BuyerHome", "Search query returned " + snapshot.getChildrenCount() + " results.");
                for (DataSnapshot child : snapshot.getChildren()) {
                    Log.d("BuyerHome", "Found item: " + child.getValue(MainModel.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BuyerHome", "Search query cancelled or failed: " + error.getMessage());
            }
        });

        FirebaseRecyclerOptions<MainModel> searchOptions =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(searchQuery, MainModel.class)
                        .build();

        mainProductAdapter.updateOptions(searchOptions);
        mainProductAdapter.startListening();  // Ensure the adapter starts listening to the new query
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        buyerEmail = intent.getStringExtra("email");
        if (buyerEmail == null) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            buyerEmail = prefs.getString(KEY_EMAIL, null);
            if (buyerEmail == null) {
                Log.e("BuyerHome", "Email is null and no saved email found on new intent. Redirecting to login.");
                redirectToLogin();
                return;
            } else {
                Log.d("BuyerHome", "Email retrieved from SharedPreferences on new intent: " + buyerEmail);
            }
        } else {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(KEY_EMAIL, buyerEmail);
            editor.apply();
            Log.d("BuyerHome", "Email received on new intent: " + buyerEmail);
        }
    }

    @Override
    public void onProductClick(String productId) {
        Intent intent = new Intent(BuyerHome.this, BuyerDetails.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainProductAdapter.startListening();
    }

    private void redirectToLogin() {
        // Redirect to the login activity
        Intent loginIntent = new Intent(BuyerHome.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
