//package com.company;

public class Security {
    private String name;
    private  String description;
    private double price;
    private long supply;

    public Security(String name, String description, double price, long supply) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.supply = supply;
    }

    //Security Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSupply(long supply) {
        this.supply = supply;
    }

    // Security Getters

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public long getSupply() {
        return supply;
    }
}
