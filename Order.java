//package com.company;
import java.time.LocalDateTime;

public class Order {
    private Security security;
    private double price;
    private long quantity;
    private String user;
    private LocalDateTime time;
    private double totalPrice;

    public Order(Security security, double price, long quantity, String user, LocalDateTime time) {
        this.setSecurity(security);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setUser(user);
        this.setTime(time);
        this.setTotalPrice(quantity * price);
    }

    // Order Setters

    public void setSecurity(Security security) {
        this.security = security;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Order Getters

    public Security getSecurity() {
        return security;
    }

    public double getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
