package com.example.model;


import com.aerospike.mapper.annotations.AerospikeExclude;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.aerospike.mapper.annotations.AerospikeReference;
import com.example.util.AerospikeDB;
import com.example.util.Utility;

import java.util.Date;

@AerospikeRecord(namespace= AerospikeDB.NAMESPACE, set= AerospikeDB.TRANSACTION)
public class Transaction {
    @AerospikeExclude
    private static int uniqueId = 1;
    @AerospikeKey
    private int transactionId;
    private String date;
    @AerospikeReference
    private Owner seller;
    @AerospikeReference
    private Owner buyer;
    @AerospikeReference
    private Property property;
    private long price;

    public Transaction(){}

    public Transaction(Owner seller, Owner buyer, Property property) {
        this.transactionId = uniqueId;
        uniqueId++;
        this.date = Utility.getCurrectDate();
        this.seller = seller;
        this.buyer = buyer;
        this.property = property;
        this.price = property.getCost();
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Owner getSeller() {
        return seller;
    }

    public void setSeller(Owner seller) {
        this.seller = seller;
    }

    public Owner getBuyer() {
        return buyer;
    }

    public void setBuyer(Owner buyer) {
        this.buyer = buyer;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
