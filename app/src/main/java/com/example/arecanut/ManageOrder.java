package com.example.arecanut;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;


import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.media.RouteListingPreference;
import android.os.Bundle;
import android.transition.ChangeTransform;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageOrder extends AppCompatActivity {
    private SearchView searchView;
    MainModel mainModel;
    RecyclerView recyclerView;
    MainAdapter mainAdapter;
    String userEmail; // Declare userEmail globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manageproduct);
//        searchView = findViewById(R.id.searchview);

        recyclerView = findViewById(R.id.rv1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get user email from the previous activity
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("email");

        // Firebase query to retrieve data matching user's email
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Details")
                .orderByChild("email")
                .equalTo(userEmail);

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(query, MainModel.class)
                        .build();

        // Initialize the adapter
        mainAdapter = new MainAdapter(options);
        recyclerView.setAdapter(mainAdapter);

        // Set the query text listener for the search view
//        searchView.setOnQueryTextListener(new OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                filterList(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterList(newText);
//                return false;
//            }
//        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mainAdapter.startListening();
    }

//    private void filterList(String text) {
//        try {
//            // FirebaseRecyclerOptions to query the filtered data from Firebase
//            FirebaseRecyclerOptions<MainModel> filteredOptions =
//                    new FirebaseRecyclerOptions.Builder<MainModel>()
//                            .setQuery(
//                                    FirebaseDatabase.getInstance()
//                                            .getReference()
//                                            .child("Details")
//                                            .orderByChild("email")  // Include filtering by email
//                                            .equalTo(userEmail)
//                                            .orderByChild("category")
//                                            .startAt(text.toLowerCase(Locale.getDefault()))
//                                            .endAt(text.toLowerCase(Locale.getDefault()) + "\uf8ff"),
//                                    MainModel.class)
//                            .build();
//
//            // Update the adapter with filtered options
//            mainAdapter.updateOptions(filteredOptions);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapter.stopListening();
    }
}
