package com.example.arecanut;

public class PriceData {
    private String category; // Only category
    private String date;
    private double price;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PriceData(String category, String date, double price) {
        this.category = category;
        this.date = date;
        this.price = price;
    }
// Add constructors, getters, and setters
}
