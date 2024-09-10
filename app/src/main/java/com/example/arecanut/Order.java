package com.example.arecanut;

import java.util.Map;

// Order class
public class Order {
    private String selectedAddress;
    private double finalPrice;

    public Order(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private double totalPrice;
    private Map<String, Object> selectedUserAddress;

    // Required empty constructor
    public Order() {
    }

    public String getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(String selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Map<String, Object> getSelectedUserAddress() {
        return selectedUserAddress;
    }

    public void setSelectedUserAddress(Map<String, Object> selectedUserAddress) {
        this.selectedUserAddress = selectedUserAddress;
    }

    // Constructor
    public Order(String selectedAddress, double finalPrice, Map<String, Object> selectedUserAddress) {
        this.selectedAddress = selectedAddress;
        this.finalPrice = finalPrice;
        this.selectedUserAddress = selectedUserAddress;
    }

    // Getters and setters (if needed)
    // ...
}
