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

// SellerReviewCart.java
// SellerReviewCart.java
public class SellerReviewCart extends RecyclerView.Adapter<SellerReviewCart.ReviewViewHolder> {

    private List<ReviewModel> reviewList;

    public SellerReviewCart(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sellerreviewcart, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel currentReview = reviewList.get(position);

        // Bind data to the ViewHolder
        holder.categoryTextView.setText(currentReview.getCategory());
        holder.ratingTextView.setText(currentReview.getRating());
        holder.reviewTextView.setText(currentReview.getReview());

        // Load image using Picasso or Glide
        Picasso.get().load(currentReview.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTextView;
        TextView ratingTextView;
        TextView reviewTextView;
        ImageView imageView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            categoryTextView = itemView.findViewById(R.id.categorytext);
            ratingTextView = itemView.findViewById(R.id.ratingtxt);
            reviewTextView = itemView.findViewById(R.id.reviewtxt);
            imageView = itemView.findViewById(R.id.img1);
        }
    }
}
