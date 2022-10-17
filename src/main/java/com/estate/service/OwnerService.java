package com.estate.service;

import com.estate.Exception.DataNotFoundException;
import com.estate.Exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.PropertyDTO;
import com.estate.repository.AerospikeAccess;
import com.estate.repository.AerospikeTransactionProcessor;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OwnerService {
    private static OwnerService INSTANCE = new OwnerService();

    private OwnerService(){}

    public static OwnerService getInstance(){
        return INSTANCE;
    }

    public ArrayList<OwnerDAO> getAllOwners(){
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        ArrayList<OwnerDAO> owners = aerospikeAccess.getSet();
//        if(owners.isEmpty())
//            throw new DataNotFoundException("There is no records for any owner in the database yet.");
        return owners;
    }

    public OwnerDAO getOwner(String userName){
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        OwnerDAO owner = aerospikeAccess.getRecord(userName);
        if(owner == null)
            throw new DataNotFoundException("Owner not found!");
        return owner;
    }

    public OwnerDAO deleteOwner(String userName){
        OwnerDAO owner = getOwner(userName);
        ArrayList<PropertyDAO> properties = PropertyService.getInstance().getPropertiesOfOwner(userName);
        if(!properties.isEmpty())
            throw new RequestFailedException("Can't delete an owner that has properties.");
        TransactionService.getInstance().deleteOwnerTransactions(owner);
        new AerospikeAccess<OwnerDAO>(OwnerDAO.class).deleteRecord(owner);
        return owner;
    }

    public OwnerDAO updateOwner(OwnerDAO ownerUpdated, String userName){
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
//        if(aerospikeAccess.getRecord(userName) == null)
//            throw new DataNotFoundException("Owner doesn't exist");
        aerospikeAccess.updateRecord(ownerUpdated, "firstName");
        return aerospikeAccess.getRecord(userName);
    }
}
