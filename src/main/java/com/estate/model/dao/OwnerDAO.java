package com.estate.model.dao;

import com.aerospike.mapper.annotations.AerospikeKey;
import com.aerospike.mapper.annotations.AerospikeRecord;
import com.estate.repository.AerospikeAccess;

/**
 * The type OwnerDAO is the owner data access object.
 * It is the object that's used between datalayer and service layer.
 */
@AerospikeRecord(namespace= AerospikeAccess.NAMESPACE, set= AerospikeAccess.OWNERSHIP)
public class OwnerDAO {
    @AerospikeKey
    private String userName;
    private String firstName;

    private String lastName;
    private long balance;

    /**
     * Instantiates a new Owner dao.
     */
    public OwnerDAO(){}

    /**
     * Instantiates a new Owner dao.
     *
     * @param userName  the username is the unique id of the owner
     * @param firstName the first name
     * @param lastName  the last name
     * @param balance   the balance
     */
    public OwnerDAO(String userName, String firstName, String lastName, long balance) {
        this();
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set username.
     *
     * @param userName the username
     */
    public void setUserName(String userName){
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
}