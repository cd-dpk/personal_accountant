package com.dpk.pa.data_models;

import com.dpk.pa.data_models.db.AccountTable;

import java.util.ArrayList;
import java.util.List;

public class Account{
    private String phone, name;
    private double givenTo=0.0, TakenFrom=0.0;

    public Account(String phone, String name, double givenTo, double takenFrom){
        setName(name);
        setPhone(phone);
        setGivenTo(givenTo);
        setTakenFrom(takenFrom);
    }
    public Account(AccountTable accountTable){
        setName(accountTable.getName());
        setPhone(accountTable.getPhone());
        setGivenTo(0.0);
        setTakenFrom(0.0);
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

    public double getGivenTo() {
        return givenTo;
    }

    public void setGivenTo(double givenTo) {
        this.givenTo = givenTo;
    }

    public double getTakenFrom() {
        return TakenFrom;
    }

    public void setTakenFrom(double takenFrom) {
        TakenFrom = takenFrom;
    }

}
