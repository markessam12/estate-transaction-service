package com.example.model.dao;

import com.aerospike.mapper.annotations.AerospikeExclude;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.example.util.AerospikeDB;

import java.util.ArrayList;
import java.util.List;

@AerospikeRecord(namespace= AerospikeDB.NAMESPACE, set= AerospikeDB.OWNERSHIP)
public class OwnerDAO {
    @AerospikeExclude
    private static int uniqueId = 1;
    @AerospikeKey
    private int accountId;
    private String firstName;

    private String lastName;
    private long balance;
    @AerospikeExclude
    public List<PropertyDAO> ownedProperties;

    public OwnerDAO(){}

    public OwnerDAO(String firstName, String lastName, long balance) {
        this.accountId = uniqueId;
        uniqueId++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
        this.ownedProperties = new ArrayList<>();
    }

    public int getId() {
        return accountId;
    }

    public void setId(String id) {
        this.accountId = Integer.parseInt(id);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void addProperty(PropertyDAO property){
        this.ownedProperties.add(property);
    }
}