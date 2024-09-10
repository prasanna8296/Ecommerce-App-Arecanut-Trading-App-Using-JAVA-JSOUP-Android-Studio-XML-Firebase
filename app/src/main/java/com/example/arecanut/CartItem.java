package com.example.arecanut;

public class CartItem {
    private String orderId;
    private String title;
    private String category;
    private String quantity;
    private String scale;
    private String price;
    private String imageUrl;
    private String selectedAddress;

    public CartItem(String orderId, String title, String category, String quantity, String scale, String price, String imageUrl, String selectedAddress) {
        this.orderId = orderId;
        this.title = title;
        this.category = category;
        this.quantity = quantity;
        this.scale = scale;
        this.price = price;
        this.imageUrl = imageUrl;
        this.selectedAddress = selectedAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getScale() {
        return scale;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSelectedAddress() {
        return selectedAddress;
    }
}
