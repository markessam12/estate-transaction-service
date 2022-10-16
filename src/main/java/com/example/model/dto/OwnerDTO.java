package com.example.model.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "owner")
public class OwnerDTO {
    @XmlAttribute
    private int accountId;
    private String firstName;

    private String lastName;
    private long balance;
    public List<PropertyDTO> ownedProperties;

    public OwnerDTO(){}

    public OwnerDTO(String firstName, String lastName, long balance) {
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

    public void addProperty(PropertyDTO property){
        this.ownedProperties.add(property);
    }
}