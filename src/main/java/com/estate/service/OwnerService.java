package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.repository.AerospikeAccess;
import java.util.ArrayList;

public class OwnerService {
    private static final OwnerService INSTANCE = new OwnerService();

    private OwnerService(){}

    public static OwnerService getInstance(){
        return INSTANCE;
    }

    public ArrayList<OwnerDAO> getAllOwners() throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        ArrayList<OwnerDAO> owners = aerospikeAccess.getSet();
        if(owners.isEmpty())
            throw new DataNotFoundException("There is no records for any owner in the database yet.");
        return owners;
    }

    public OwnerDAO getOwner(String userName) throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        OwnerDAO owner = aerospikeAccess.getRecord(userName);
        if(owner == null)
            throw new DataNotFoundException("Owner not found!");
        return owner;
    }

    public OwnerDAO deleteOwner(String userName) throws RequestFailedException, DataNotFoundException {
        OwnerDAO owner = getOwner(userName);
        if(isOwnerHasProperties(userName))
            throw new RequestFailedException("Can't delete an owner that has properties.");
        TransactionService.getInstance().deleteOwnerTransactions(owner);
        new AerospikeAccess<>(OwnerDAO.class).deleteRecord(owner);
        return owner;
    }

    public boolean isOwnerHasProperties(String ownerUserName){
        try{
            //The next line will throw DataNotFoundException if owner has no properties
            PropertyService.getInstance().getPropertiesOfOwner(ownerUserName);
            return true;
        } catch (DataNotFoundException ignored) {
            return false;
        }
    }

    public OwnerDAO updateOwner(OwnerDAO ownerUpdated, String userName) throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        if(aerospikeAccess.getRecord(userName) == null)
            throw new DataNotFoundException("Owner doesn't exist");
        aerospikeAccess.updateRecord(ownerUpdated, "firstName");
        return aerospikeAccess.getRecord(userName);
    }

    public OwnerDAO addToOwnerBalance(OwnerDAO ownerDAO, long additionalBalance) throws DataNotFoundException{
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        if(ownerDAO == null)
            throw new DataNotFoundException("Owner doesn't exist");
        ownerDAO.setBalance(ownerDAO.getBalance() + additionalBalance);
        aerospikeAccess.updateRecord(ownerDAO, "balance");
        return ownerDAO;
    }

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
