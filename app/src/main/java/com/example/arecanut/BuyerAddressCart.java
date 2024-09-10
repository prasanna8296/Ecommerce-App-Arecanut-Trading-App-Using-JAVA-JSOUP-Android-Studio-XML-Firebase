package com.example.arecanut;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class BuyerAddressCart extends RecyclerView.Adapter<BuyerAddressCart.ViewHolder> {

    private List<UserAddress> userAddresses;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnItemClickListener itemClickListener;

    public interface OnEditClickListener {
        void onEditClick(int position);
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public BuyerAddressCart(List<UserAddress> userAddresses, OnEditClickListener editClickListener,
                            OnDeleteClickListener deleteClickListener, OnItemClickListener itemClickListener) {
        this.userAddresses = userAddresses;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
        this.itemClickListener = itemClickListener; // Initialize the listener
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_address_ada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final UserAddress userAddress = userAddresses.get(position);

        // Concatenate address and address1
        String fullAddress = userAddress.getAddress() + " " + userAddress.getAddress1();

        // Set data to views in your card view
        holder.nameTextView.setText(userAddress.getName());
        holder.addressTextView.setText(fullAddress);
        holder.phoneNoTextView.setText(userAddress.getPhoneNo());

        // Set click listeners for edit and delete buttons
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editClickListener != null) {
                    editClickListener.onEditClick(position);
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(position);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAddresses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, phoneNoTextView;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nametxt);
            addressTextView = itemView.findViewById(R.id.adresstxt);
            phoneNoTextView = itemView.findViewById(R.id.phonetxt);
            editButton = itemView.findViewById(R.id.button3);
            deleteButton = itemView.findViewById(R.id.button4);
        }
    }
}
