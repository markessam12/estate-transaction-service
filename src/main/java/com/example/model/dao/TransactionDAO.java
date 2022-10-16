package com.example.model.dao;

import com.aerospike.mapper.annotations.AerospikeExclude;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.aerospike.mapper.annotations.AerospikeReference;
import com.example.util.AerospikeDB;
import com.example.util.Utility;

@AerospikeRecord(namespace= AerospikeDB.NAMESPACE, set= AerospikeDB.TRANSACTION)
public class TransactionDAO {
    @AerospikeExclude
    private static int uniqueId = 1;
    @AerospikeKey
    private int transactionId;
    private String date;
    @AerospikeReference
    private OwnerDAO seller;
    @AerospikeReference
    private OwnerDAO buyer;
    @AerospikeReference
    private PropertyDAO property;
    private long price;

    public TransactionDAO(){}

    public TransactionDAO(OwnerDAO seller, OwnerDAO buyer, PropertyDAO property) {
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

    public OwnerDAO getSeller() {
        return seller;
    }

    public void setSeller(OwnerDAO seller) {
        this.seller = seller;
    }

    public OwnerDAO getBuyer() {
        return buyer;
    }

    public void setBuyer(OwnerDAO buyer) {
        this.buyer = buyer;
    }

    public PropertyDAO getProperty() {
        return property;
    }

    public void setProperty(PropertyDAO property) {
        this.property = property;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
