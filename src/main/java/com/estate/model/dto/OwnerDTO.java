package com.estate.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "owner")
@XmlAccessorType(XmlAccessType.FIELD)
public class OwnerDTO extends HATEOAS {
    private String userName;
    private String firstName;

    private String lastName;
    private long balance;
    private List<PropertyDTO> ownedProperties;

    public OwnerDTO(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void setOwnedProperties(List<PropertyDTO> properties){
        this.ownedProperties = properties;
    }

    public List<PropertyDTO> getOwnedProperties() {
        return ownedProperties;
    }
}