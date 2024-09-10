package com.example.arecanut;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserAddress implements Serializable {
    private String name;
    private String id;
    private String address;
    private String address1; // New field
    private String phoneNo;

    public UserAddress() {
        // Default constructor required for DataSnapshot.getValue(UserAddress.class)
    }

    public UserAddress(String name, String address, String address1, String phoneNo, String id) {
        this.name = name;
        this.address = address;
        this.address1 = address1;
        this.phoneNo = phoneNo;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress1() {
        return address1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPhoneNo() {
        return phoneNo;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("address", address);
        map.put("address1", address1);
        // Add other fields as needed
        return map;
    }
}
