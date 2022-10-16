package com.example.model.dao;

import com.aerospike.mapper.annotations.AerospikeExclude;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.aerospike.mapper.annotations.AerospikeReference;
import com.example.util.AerospikeDB;

@AerospikeRecord(namespace= AerospikeDB.NAMESPACE, set = AerospikeDB.PROPERTY)
public class PropertyDAO {
    @AerospikeExclude
    private static int uniqueId = 1;
    @AerospikeKey
    private int propertyId;
    private String address;
    @AerospikeReference
    private OwnerDAO propertyOwner;
    private long cost;
    private int forSale;

    public PropertyDAO(){}

    public PropertyDAO(String address, OwnerDAO propertyOwner, long cost) {
        this.propertyId = uniqueId;
        uniqueId++;
        this.address = address;
        this.propertyOwner = propertyOwner;
//        propertyOwner.addProperty(this);
        this.cost = cost;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public OwnerDAO getPropertyOwner() {
        return propertyOwner;
    }

    public void setPropertyOwner(OwnerDAO propertyOwner) {
        this.propertyOwner = propertyOwner;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }


    public int getForSale() {
        return forSale;
    }

    public void setForSale(int forSale) {
        this.forSale = forSale;
    }

}
