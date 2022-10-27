package com.estate.model.dao;

import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.aerospike.mapper.annotations.AerospikeReference;
import com.estate.repository.AerospikeAccess;
import com.estate.util.Utility;

@AerospikeRecord(namespace= AerospikeAccess.NAMESPACE, set= AerospikeAccess.TRANSACTION)
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
        this.date = Utility.getCurrentDate();
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
