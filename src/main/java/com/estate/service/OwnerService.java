package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.repository.AerospikeAccess;
import java.util.ArrayList;

/**
 * The singleton class representing the owner service layer and
 * providing all owner services to the Controller layer
 */
public interface OwnerService {
    /**
     * Gets all owners inside the database.
     *
     * @return the list of all owners
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<OwnerDAO> getAllOwners() throws DataNotFoundException;

    /**
     * Gets a specific owner from the database.
     *
     * @param userName the unique username of the owner
     * @return the owner
     * @throws DataNotFoundException the data not found exception
     */
    public OwnerDAO getOwner(String userName) throws DataNotFoundException;

    /**
     * Delete a specific owner record from database.
     *
     * @param userName the unique username of the owner
     * @return the deleted owner
     * @throws RequestFailedException the request failed exception which happens due to a precondition failure
     * @throws DataNotFoundException  the data not found exception
     */
    public OwnerDAO deleteOwner(String userName) throws RequestFailedException, DataNotFoundException;

    /**
     * Update a specific owner record.
     *
     * @param ownerUpdated the updated owner data sent from the controller layer
     * @param userName     the unique owner username
     * @return the updated owner data retrieved from the database
     * @throws DataNotFoundException the data not found exception
     */
    public OwnerDAO updateOwner(OwnerDAO ownerUpdated, String userName) throws DataNotFoundException;

    /**
     * Adds balance to a specific owner
     *
     * @param ownerDAO          the owner object
     * @param additionalBalance the additional balance to ada
     * @return the updated owner data
     * @throws DataNotFoundException the data not found exception
     */
    public OwnerDAO addToOwnerBalance(OwnerDAO ownerDAO, long additionalBalance) throws DataNotFoundException;

    /**
     * Add a new owner record to the database.
     *
     * @param userName  the new owner's username
     * @param firstName the first name
     * @param lastName  the last name
     * @param balance   the balance
     * @return the newly added owner data
     * @throws DataAlreadyExistsException the data already exists exception
     */
    public OwnerDAO addOwner(String userName, String firstName, String lastName, long balance) throws DataAlreadyExistsException;
}
