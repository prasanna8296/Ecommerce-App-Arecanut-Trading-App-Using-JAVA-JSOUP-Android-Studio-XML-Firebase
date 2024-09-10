package com.example.arecanut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ManageCartAdapter extends RecyclerView.Adapter<ManageCartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private OnItemClickListener listener;

    public ManageCartAdapter(List<CartItem> cartItemList, OnItemClickListener listener) {
        this.cartItemList = cartItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mangagecartadapter, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem currentItem = cartItemList.get(position);

        holder.categoryTextView.setText(currentItem.getTitle());
        holder.qualityTextView.setText(currentItem.getCategory());
        holder.quantityTextView.setText(currentItem.getQuantity());
        holder.spinner2TextView.setText(currentItem.getScale());
        holder.priceTextView.setText(currentItem.getPrice());
        holder.deliveryDetailsTextView.setText(currentItem.getSelectedAddress());

        Picasso.get().load(currentItem.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView categoryTextView;
        TextView qualityTextView;
        TextView quantityTextView;
        TextView spinner2TextView;
        TextView priceTextView;
        ImageView imageView;
        TextView deliveryDetailsTextView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categorytext);
            qualityTextView = itemView.findViewById(R.id.qualitytext);
            quantityTextView = itemView.findViewById(R.id.quantitytext);
            spinner2TextView = itemView.findViewById(R.id.spinner2txt);
            priceTextView = itemView.findViewById(R.id.pricetext);
            imageView = itemView.findViewById(R.id.img1);
            deliveryDetailsTextView = itemView.findViewById(R.id.txtdeliverydetails);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CartItem clickedItem = cartItemList.get(position);
                listener.onItemClick(clickedItem.getOrderId());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String orderId);
    }
}
