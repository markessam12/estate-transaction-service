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
public class OwnerService {
    private static final OwnerService INSTANCE = new OwnerService();

//    private static final Logger logger
//        = LoggerFactory.getLogger(OwnerService.class);

    private OwnerService(){}

    /**
     * Get the singleton instance of the owner service.
     *
     * @return the owner service instance
     */
    public static OwnerService getInstance(){
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
        if(owners.isEmpty())
            throw new DataNotFoundException("There is no records for any owner in the database yet.");
//        logger.info("Owners list retrieved from database");
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
        if(owner == null)
            throw new DataNotFoundException("Owner not found!");
//        logger.info("Owner {} was retrieved from database",owner.getUserName());
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
            throw new RequestFailedException("Can't delete an owner that has properties.");
        }

        try{
            TransactionService.getInstance().deleteOwnerTransactions(owner);
//            logger.warn("All transactions of owner {} was deleted from database", owner.getUserName());
        }catch (DataNotFoundException ignored){
            //This means the owner already has no transactions to delete
        }

        new AerospikeAccess<>(OwnerDAO.class).deleteRecord(owner);
//        logger.info("Owner {} was deleted from database",owner.getUserName());
        return owner;
    }

    /**
     * Checks if an owner currently owns any properties.
     *
     * @param ownerUserName the owner username
     * @return the boolean result
     */
    public boolean isOwnerHasProperties(String ownerUserName){
        try{
            //The next line will throw DataNotFoundException if owner has no properties
            PropertyService.getInstance().getPropertiesOfOwner(ownerUserName);
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
        if(aerospikeAccess.getRecord(userName) == null)
            throw new DataNotFoundException("Owner doesn't exist");
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
        if(ownerDAO == null)
            throw new DataNotFoundException("Owner doesn't exist");
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
            OwnerDAO owner = OwnerService.getInstance().getOwner(userName);
            Boolean ownerExists = owner != null;
            if(Boolean.TRUE.equals(ownerExists))
                throw new DataAlreadyExistsException("The owner already exists, use different username.");
        } catch (DataNotFoundException ignored) {
            //It's a requirement for the data to not exist.
            //So this is the correct flow, and we ignore the catch
        }
        OwnerDAO newOwner = new OwnerDAO(userName, firstName, lastName, balance);
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        aerospikeAccess.saveRecord(newOwner);
        return newOwner;
    }
}
