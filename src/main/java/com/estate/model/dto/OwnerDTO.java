package com.estate.model.dto;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * The type OwnerDTO is the owner data transfer object.
 * It is the object that's used between service layer and controller layer.
 */
@XmlRootElement(name = "owner")
public class OwnerDTO extends HATEOAS {
    private String userName;
    private String firstName;

    private String lastName;
    private long balance;
    private List<PropertyDTO> ownedProperties;

    /**
     * Instantiates a new Owner dto.
     */
    public OwnerDTO(){}

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets username.
     *
     * @param userName the username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets balance.
     *
     * @return the balance
     */
    public long getBalance() {
        return balance;
    }

    /**
     * Sets balance.
     *
     * @param balance the balance
     */
    public void setBalance(long balance) {
        this.balance = balance;
    }

    /**
     * Set owned properties.
     * This attribute is used for presenting the properties of the owner on rest calls.
     *
     * @param properties the properties
     */
    public void setOwnedProperties(List<PropertyDTO> properties){
        this.ownedProperties = properties;
    }

    /**
     * Gets owned properties.
     *
     * @return the owned properties
     */
    public List<PropertyDTO> getOwnedProperties() {
        return ownedProperties;
    }
}