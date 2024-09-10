package com.example.arecanut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class BuyerNotification extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {
    private NotificationAdapter notificationAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyer_notification);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<MainModel> options =
                new FirebaseRecyclerOptions.Builder<MainModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Details")
                                .orderByKey().limitToLast(3), MainModel.class)
                        .build();

        notificationAdapter = new NotificationAdapter(this, options, this);
        recyclerView.setAdapter(notificationAdapter);
    }

    @Override
    public void onProductClick(String productId) {
        Intent intent = new Intent(BuyerNotification.this, BuyerDetails.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notificationAdapter.startListening();
    }
}


