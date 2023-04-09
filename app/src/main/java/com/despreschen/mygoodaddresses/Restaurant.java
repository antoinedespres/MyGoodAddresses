package com.despreschen.mygoodaddresses;

public class Restaurant {
    private String name;
    private String type;
    private int number;
    private String street;
    private String postalCode;
    private String city;
    private int imageResourceId;

    public Restaurant(String name, String type, int number, String street, String postalCode, String city, int imageResourceId) {
        this.name = name;
        this.type = type;
        this.number = number;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.imageResourceId = imageResourceId;
    }

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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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
