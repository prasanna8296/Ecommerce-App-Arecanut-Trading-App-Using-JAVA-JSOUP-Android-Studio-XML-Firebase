package com.example.arecanut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<MainModel> cartList;
    private Context context;

    public CartAdapter(Context context, List<MainModel> cartList) {
        this.context = context;
        this.cartList = cartList;
    }
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onTotalPriceChange(double totalPrice);
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_design, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MainModel cartItem = cartList.get(position);

        holder.categoryText.setText(cartItem.getTitle());
        holder.qualityText.setText(cartItem.getCategory());
        holder.quantityText.setText(cartItem.getQuantity());
        holder.spinner2Text.setText(cartItem.getScale());
        holder.priceText.setText(cartItem.getPrice());

        // Load image using Picasso or Glide
        Picasso.get().load(cartItem.getImageUrl()).into(holder.mainImage);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryToDelete = cartItem.getCategory();

                // Query Firebase to find items with the same category
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart");
                Query query = cartRef.orderByChild("category").equalTo(categoryToDelete);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }

                        // Notify the listener (activity) about the deletion
                        if (listener != null) {
                            listener.onDeleteClick(position);

                            // Notify about the total price change
                            listener.onTotalPriceChange(getTotalPrice());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }
        });

    }
    private double getTotalPrice() {
        double totalPrice = 0;
        for (MainModel cartItem : cartList) {
            totalPrice += cartItem.getTotal();
        }
        return totalPrice;
    }



    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryText, qualityText, quantityText, spinner2Text, priceText;
        public ImageView mainImage;
        public Button deleteButton;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categorytext);
            qualityText = itemView.findViewById(R.id.qualitytext);
            quantityText = itemView.findViewById(R.id.quantitytext);
            spinner2Text = itemView.findViewById(R.id.spinner2txt);
            priceText = itemView.findViewById(R.id.pricetext);
            mainImage = itemView.findViewById(R.id.img1);
            deleteButton = itemView.findViewById(R.id.txtdelete);
        }
    }
}

