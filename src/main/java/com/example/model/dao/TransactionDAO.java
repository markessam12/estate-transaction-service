package com.example.model.dao;

import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.aerospike.mapper.annotations.AerospikeReference;
import com.example.util.AerospikeDB;
import com.example.util.Utility;

@AerospikeRecord(namespace= AerospikeDB.NAMESPACE, set= AerospikeDB.TRANSACTION)
public class TransactionDAO {
    @AerospikeKey
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
        this();
        this.date = Utility.getCurrectDate();
        this.seller = seller;
        this.buyer = buyer;
        this.property = property;
        this.price = property.getCost();
    }

    public String getDate() {
        return date;
    }

    public OwnerDAO getSeller() {
        return seller;
    }

    public OwnerDAO getBuyer() {
        return buyer;
    }

    public PropertyDAO getProperty() {
        return property;
    }


    public long getPrice() {
        return price;
    }

}
