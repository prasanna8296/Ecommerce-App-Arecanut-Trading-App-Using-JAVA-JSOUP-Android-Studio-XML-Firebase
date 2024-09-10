package com.example.arecanut;

import android.widget.TextView;

public class Seller {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String adhar;
    private String pan;
    private String address;

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    private String profilePictureUrl;

    // Default constructor required for Firebase
    public Seller() {
    }

    public Seller(String name, String email, String phone, String password, String adhar, String pan, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.adhar = adhar;
        this.pan = pan;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdhar() {
        return adhar;
    }

    public void setAdhar(String adhar) {
        this.adhar = adhar;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
// Getter and setter methods for each field...

    // Add getters and setters as needed for each field
    public String getName() {
        return name;
    }
}
