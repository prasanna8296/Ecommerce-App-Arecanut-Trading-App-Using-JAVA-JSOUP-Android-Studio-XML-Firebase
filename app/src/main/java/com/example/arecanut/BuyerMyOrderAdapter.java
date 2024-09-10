package com.example.arecanut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BuyerMyOrderAdapter extends RecyclerView.Adapter<BuyerMyOrderAdapter.ViewHolder> {

    private List<DataSnapshot> dataSnapshotList;
    private Context context;

    public BuyerMyOrderAdapter(List<DataSnapshot> dataSnapshotList, Context context) {
        this.dataSnapshotList = dataSnapshotList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_orders, parent, false);
        return new ViewHolder(view);
    }

    // Inside onBindViewHolder method in BuyerMyOrderAdapter

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DataSnapshot dataSnapshot = dataSnapshotList.get(position);

        // Extract data from dataSnapshot and bind it to the views in the CardView
        String title = dataSnapshot.child("title").getValue(String.class);
        String category = dataSnapshot.child("category").getValue(String.class);
        String quantity = dataSnapshot.child("quantity").getValue(String.class);
        String scale = dataSnapshot.child("scale").getValue(String.class);
        String price = dataSnapshot.child("price").getValue(String.class);
        String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
        String selectedAddress = dataSnapshot.child("selectedAddress").getValue(String.class);

        holder.categoryText.setText(title);
        holder.qualityText.setText(category);
        holder.priceText.setText(price);
        Picasso.get().load(imageUrl).into(holder.imageView);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected order
                DataSnapshot selectedOrder = dataSnapshotList.get(position);

                // Create an OrderModel object and populate it with data
                OrderModel order = new OrderModel();
                order.setTitle(title);
                order.setCategory(category);
                order.setQuantity(quantity);
                order.setScale(scale);
                order.setPrice(price);
                order.setImageUrl(imageUrl);
                order.setSelectedAddress(selectedAddress);

                // Create an Intent to start the BuyerMyOrderView activity
                Intent intent = new Intent(context, BuyerMyorderView.class);
                intent.putExtra("order", order);

                // Start the activity
                context.startActivity(intent);
            }
        });
        // Bind other data as needed
    }


    @Override
    public int getItemCount() {
        return dataSnapshotList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        TextView qualityText;
        TextView priceText;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categorytext);
            qualityText = itemView.findViewById(R.id.qualitytext);
            priceText = itemView.findViewById(R.id.pricetext);
            imageView = itemView.findViewById(R.id.img1);
        }
    }
}
