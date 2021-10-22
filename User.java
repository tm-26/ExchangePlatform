//package com.company;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private boolean type;
    private double money;
    private ArrayList<Security> securities;
    private boolean approved;

    public User(String username, String password, boolean type, double money, ArrayList<Security> securities){
        this.setUsername(username);
        this.setPassword(password);
        this.setType(type);
        this.setMoney(money);
        this.setSecurities(securities);
        this.setApproved(false);
    }

    //User Setters

    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }
    // False --> user of type trader
    // True --> user of type lister
    public void setType(boolean type){
        this.type = type;
    }

    public void setMoney(double money){
        this.money = money;
    }

    public void setSecurities(ArrayList<Security> securities){
        this.securities = securities;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    //User Getters

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public boolean getType(){
        return type;
    }

    public double getMoney(){
        return money;
    }

    public ArrayList<Security> getSecurities() {
        return securities;
    }

    public boolean isApproved() {
        return approved;
    }
}
