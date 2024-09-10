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
import com.squareup.picasso.Picasso;

public class ProductAdapter extends FirebaseRecyclerAdapter<MainModel, ProductAdapter.ViewHolder> {

    private FirebaseRecyclerOptions<MainModel> options;

    public interface OnProductClickListener {
        void onProductClick(String productId);
    }

    private final Context context;
    private final OnProductClickListener onProductClickListener;

    public ProductAdapter(Context context, FirebaseRecyclerOptions<MainModel> options, OnProductClickListener onProductClickListener) {
        super(options);
        this.context = context;
        this.onProductClickListener = onProductClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView category, quantity, quality, price, description, txtLocation, title,scale;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            title = itemView.findViewById(R.id.categorytext);
            quantity = itemView.findViewById(R.id.quantitytext);
            category = itemView.findViewById(R.id.qualitytext);
            price = itemView.findViewById(R.id.pricetext);
            scale = itemView.findViewById(R.id.spinner2txt);
            txtLocation = itemView.findViewById(R.id.txtLocation);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MainModel model) {
        holder.title.setText(model.getTitle());
        holder.category.setText(model.getCategory());
        holder.scale.setText(model.getScale());
        holder.quantity.setText(model.getQuantity());
        holder.price.setText(model.getPrice());
        holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.get().load(model.getImageUrl()).into(holder.img);
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference().child("Details").child(getRef(position).getKey()).child("address");
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String address = snapshot.getValue(String.class);
                    holder.txtLocation.setText(address);
                } else {
                    holder.txtLocation.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.txtLocation.setText("Failed to retrieve address");
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductClick(getRef(position).getKey());
            }
        });
    }
    public void updateOptions(FirebaseRecyclerOptions<MainModel> options) {
        this.options = options;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_product, parent, false);
        return new ViewHolder(view);
    }
}
