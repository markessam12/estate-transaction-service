package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.repository.AerospikeAccess;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The singleton class representing the owner service layer and
 * providing all owner services to the Controller layer
 */
public class OwnerServiceImp implements OwnerService{
    private static final OwnerServiceImp INSTANCE = new OwnerServiceImp();

    private static final Logger logger
        = LoggerFactory.getLogger(OwnerServiceImp.class);


    private OwnerServiceImp(){}

    /**
     * Get the singleton instance of the owner service.
     *
     * @return the owner service instance
     */
    public static OwnerServiceImp getInstance(){
        return INSTANCE;
    }

    /**
     * Gets all owners inside the database.
     *
     * @return the list of all owners
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<OwnerDAO> getAllOwners() throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        ArrayList<OwnerDAO> owners = aerospikeAccess.getSet();
        if(owners.isEmpty()){
            logger.warn("No records of owners found on the system");
            throw new DataNotFoundException("There is no records for any owner in the database yet.");
        }
        return owners;
    }

    /**
     * Gets a specific owner from the database.
     *
     * @param userName the unique username of the owner
     * @return the owner
     * @throws DataNotFoundException the data not found exception
     */
    public OwnerDAO getOwner(String userName) throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        OwnerDAO owner = aerospikeAccess.getRecord(userName);
        if(owner == null){
            logger.warn("Owner of username {} was not found!",userName);
            throw new DataNotFoundException("Owner not found!");
        }
        return owner;
    }

    /**
     * Delete a specific owner record from database.
     *
     * @param userName the unique username of the owner
     * @return the deleted owner
     * @throws RequestFailedException the request failed exception which happens due to a precondition failure
     * @throws DataNotFoundException  the data not found exception
     */
    public OwnerDAO deleteOwner(String userName) throws RequestFailedException, DataNotFoundException {
        OwnerDAO owner = getOwner(userName);
        if(isOwnerHasProperties(userName)){
            logger.warn("failed to delete owner {}, he owns properties",userName);
            throw new RequestFailedException("Can't delete an owner that has properties.");
        }
        try{
            TransactionServiceImp.getInstance().deleteOwnerTransactions(owner);
        }catch (DataNotFoundException ignored){
            //This means the owner already has no transactions to delete
            logger.info("Owner {} has no transactions, it will be directly deleted",userName);
        }

        new AerospikeAccess<>(OwnerDAO.class).deleteRecord(owner);
        return owner;
    }

    /**
     * Checks if an owner currently owns any properties.
     *
     * @param ownerUserName the owner username
     * @return the boolean result
     */
    private boolean isOwnerHasProperties(String ownerUserName){
        try{
            //The next line will throw DataNotFoundException if owner has no properties
            PropertyServiceImp.getInstance().getPropertiesOfOwner(ownerUserName);
            return true;
        } catch (DataNotFoundException ignored) {
            return false;
        }
    }

    /**
     * Update a specific owner record.
     *
     * @param ownerUpdated the updated owner data sent from the controller layer
     * @param userName     the unique owner username
     * @return the updated owner data retrieved from the database
     * @throws DataNotFoundException the data not found exception
     */
    public OwnerDAO updateOwner(OwnerDAO ownerUpdated, String userName) throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        if(aerospikeAccess.getRecord(userName) == null){
            logger.warn("failed to update owner {}, owner does not exists", userName);
            throw new DataNotFoundException("Owner doesn't exist");
        }
        ownerUpdated.setUserName(userName);
        aerospikeAccess.updateRecord(ownerUpdated, "firstName", "lastName");
        return aerospikeAccess.getRecord(userName);
    }

    /**
     * Adds balance to a specific owner
     *
     * @param ownerDAO          the owner object
     * @param additionalBalance the additional balance to ada
     * @return the updated owner data
     * @throws DataNotFoundException the data not found exception
     */
    public OwnerDAO addToOwnerBalance(OwnerDAO ownerDAO, long additionalBalance) throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        if(ownerDAO == null){
            logger.warn("failed add balance, owner does not exists");
            throw new DataNotFoundException("Owner doesn't exist");
        }
        ownerDAO.setBalance(ownerDAO.getBalance() + additionalBalance);
        aerospikeAccess.updateRecord(ownerDAO, "balance");
        return ownerDAO;
    }

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
    public OwnerDAO addOwner(String userName, String firstName, String lastName, long balance) throws DataAlreadyExistsException{
        try{
            OwnerDAO owner = OwnerServiceImp.getInstance().getOwner(userName);
            Boolean ownerExists = owner != null;
            if(Boolean.TRUE.equals(ownerExists)){
                logger.warn("Failed to add new owner of username {}, owner already exists.", userName);
                throw new DataAlreadyExistsException("The owner already exists, use different username.");
            }
        } catch (DataNotFoundException ignored) {
            //It's a requirement that there is no existing owner of the same data
            //So this is the correct flow, and we ignore the catch
            logger.info("Adding new owner {}", userName);
        }
        OwnerDAO newOwner = new OwnerDAO(userName, firstName, lastName, balance);
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        aerospikeAccess.saveRecord(newOwner);
        return newOwner;
    }
}
