package com.example.model;

import com.aerospike.mapper.annotations.AerospikeExclude;
import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.example.util.AerospikeDB;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "owner")
@AerospikeRecord(namespace= AerospikeDB.NAMESPACE, set= AerospikeDB.OWNERSHIP)
public class Owner {
    @AerospikeExclude
    private static int uniqueId = 1;
    @AerospikeKey
    @XmlAttribute
    private int accountId;
    private String firstName;

    private String lastName;
    private long balance;
    @AerospikeExclude
    public List<Property> ownedProperties;

    public Owner(){}

    public Owner(String firstName, String lastName, int balance) {
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

    public void addProperty(Property property){
        this.ownedProperties.add(property);
    }
}