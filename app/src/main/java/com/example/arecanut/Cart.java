package com.example.arecanut;

public class Cart {
    private String productId;
    private String title;
    private String category;
    private String quantity;
    private String scale;
    private String price;
    private String imageUrl;
    private String email;

    public Cart(String productId, String title, String category, String quantity,
                String scale, String price, String imageUrl, String email) {
        this.productId = productId;
        this.title = title;
        this.category = category;
        this.quantity = quantity;
        this.scale = scale;
        this.price = price;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public double getTotal() {
        // Assuming you have 'price' and 'quantity' as String, convert them to double and calculate total
        try {
            double priceValue = Double.parseDouble(price);
            double quantityValue = Double.parseDouble(quantity);

            return priceValue * quantityValue;
        } catch (NumberFormatException e) {
            // Handle the case where price or quantity is not a valid double
            e.printStackTrace();
            return 0.0;
        }
    }
// Add getters and setters as needed
}
