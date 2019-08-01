package com.dpk.pa.data_models;

public class Account {
    private String phone, name;
    private double deposit, due;

    public Account(String phone, String name, double deposit, double due){
        setName(name);
        setPhone(phone);
        setDeposit(deposit);
        setDue(due);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }
}
