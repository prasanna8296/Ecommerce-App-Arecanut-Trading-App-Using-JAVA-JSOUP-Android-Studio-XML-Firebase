package com.example.arecanut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationAdapter extends FirebaseRecyclerAdapter<MainModel, NotificationAdapter.NotificationViewHolder> {

    public interface OnNotificationClickListener {
        void onProductClick(String productId);
    }

    private final Context context;
    private final OnNotificationClickListener onNotificationClickListener;

    public NotificationAdapter(Context context, FirebaseRecyclerOptions<MainModel> options, BuyerNotification onNotificationClickListener) {
        super(options);
        this.context = context;
        this.onNotificationClickListener = (OnNotificationClickListener) onNotificationClickListener;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView category, quantity, quality, price, description, locationTextView, title, scale;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categorytext);
            quantity = itemView.findViewById(R.id.quantitytext);
            category = itemView.findViewById(R.id.qualitytext);
//            price = itemView.findViewById(R.id.pricetext);
            scale = itemView.findViewById(R.id.spinner2txt);
            locationTextView = itemView.findViewById(R.id.txtLocation);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull MainModel model) {
        holder.title.setText(model.getTitle());
        holder.category.setText(model.getCategory());
        holder.scale.setText(model.getScale());
        holder.quantity.setText(model.getQuantity());
//        holder.price.setText(model.getPrice());

        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference()
                .child("Details")
                .child(getRef(position).getKey())
                .child("address");

        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String address = snapshot.getValue(String.class);
                    holder.locationTextView.setText(address);
                } else {
                    holder.locationTextView.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.locationTextView.setText("Failed to retrieve address");
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onNotificationClickListener != null) {
                onNotificationClickListener.onProductClick(getRef(position).getKey());
            }
        });
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification, parent, false);
        return new NotificationViewHolder(view);
    }
}
