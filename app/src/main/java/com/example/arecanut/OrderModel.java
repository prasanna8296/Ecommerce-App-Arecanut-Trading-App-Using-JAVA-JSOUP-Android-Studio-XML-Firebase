package com.example.arecanut;

import java.io.Serializable;

public class OrderModel implements Serializable {
    private String title;
    private String category;
    private String quantity;
    private String scale;
    private String price;
    private String imageUrl;

    public OrderModel(String selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    private String selectedAddress;

    public OrderModel() {

    }

    public String getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(String selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public OrderModel(String title, String category, String quantity, String scale, String price, String imageUrl) {
        this.title = title;
        this.category = category;
        this.quantity = quantity;
        this.scale = scale;
        this.price = price;
        this.imageUrl = imageUrl;
    }
// Add more fields as needed

    // Default constructor (required for Firebase)
}