package com.example.thuc_tap_tmdd_fpoly.model;

public class Discount {
    private String code;
    private double amount;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Discount(){

    }

    public Discount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
