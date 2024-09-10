package com.example.arecanut;

public class HelperClass2 {
    private String name;
    private String phone;
    private String email;
    private String password;

    // Default constructor for Firebase
    public HelperClass2() {
    }

    // Constructor with parameters
    public HelperClass2(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
// Add getters and setters if not already present
    // ...
}
