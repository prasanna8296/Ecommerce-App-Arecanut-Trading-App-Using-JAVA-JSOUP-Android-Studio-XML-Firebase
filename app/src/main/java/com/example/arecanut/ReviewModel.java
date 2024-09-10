package com.example.arecanut;

public class ReviewModel {
        private String category;
        private String rating;
        private String review;
        private String imageUrl;

        public ReviewModel(String category, Long rating, String review, String imageUrl) {
            this.category = category;
            this.rating = String.valueOf(rating);
            this.review = review;
            this.imageUrl = imageUrl;
        }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Add getters and setters as needed
    }


