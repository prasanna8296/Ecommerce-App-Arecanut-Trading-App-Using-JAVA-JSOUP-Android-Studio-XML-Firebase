package com.example.arecanut;

import android.widget.Button;

import java.io.Serializable;

public class MainModel implements Serializable {

    private String title;
    private String productId;
    private String category;
    private String scale;
    private String quantity;
    private String price;
    private String description;
    private String imageUrl;
    private Button btn1;
    private Button btn2;
    private double total;
    private String location;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private  String email;

    public MainModel() {
        // Default constructor
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public MainModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MainModel(String title, String category, String scale) {
        this.title = title;
        this.category = category;
        this.scale = scale;
    }

    public MainModel(String title, Button btn1, Button btn2) {
        this.title = title;
        this.btn1 = btn1;
        this.btn2 = btn2;
    }

    public MainModel(String title, String quantity, String price, String description) {
        this.title = title;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }

    // Getter and Setter methods...



    // Getter and Setter methods for other fields...

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

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Button getBtn1() {
        return btn1;
    }

    public void setBtn1(Button btn1) {
        this.btn1 = btn1;
    }

    public Button getBtn2() {
        return btn2;
    }

    public void setBtn2(Button btn2) {
        this.btn2 = btn2;
    }

    public double getTotal() {
        // Assuming 'price' is a numeric field
        double total = Double.parseDouble(this.getPrice());
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}


