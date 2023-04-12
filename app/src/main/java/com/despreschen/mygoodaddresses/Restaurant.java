package com.despreschen.mygoodaddresses;

public class Restaurant {

    private int id;
    private String name;
    private String type;
    private String addressLine;
    private String postalCode;
    private String city;
    private int imageResourceId;

    public Restaurant(int id, String name, String type, String addressLine, String postalCode, String city) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.addressLine = addressLine;
        this.postalCode = postalCode;
        this.city = city;
        this.imageResourceId = R.drawable.ic_restaurant_plate;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
