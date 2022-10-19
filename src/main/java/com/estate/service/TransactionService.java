package com.estate.service;

import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.TransactionDTO;
import com.estate.repository.AerospikeAccess;
import jakarta.ws.rs.core.UriInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransactionService {
    private static final TransactionService INSTANCE = new TransactionService();

    private TransactionService(){}

    public static TransactionService getInstance(){
        return INSTANCE;
    }

    public ArrayList<TransactionDAO> getTransactions() throws DataNotFoundException {
        AerospikeAccess<TransactionDAO> aerospikeAccess = new AerospikeAccess<>(TransactionDAO.class);
        ArrayList<TransactionDAO> transactions = aerospikeAccess.getSet();
        if(transactions.isEmpty())
            throw new DataNotFoundException("There are no transactions.");
        return transactions;
    }

    public void deleteOwnerTransactions(OwnerDAO owner) throws DataNotFoundException{
        ArrayList<TransactionDAO> ownerTransactions = getOwnerTransactions(owner);
        new AerospikeAccess<>(TransactionDAO.class).deleteRecords(ownerTransactions);
    }

    public ArrayList<TransactionDAO> getOwnerTransactions(OwnerDAO owner) throws DataNotFoundException {
        AerospikeAccess<TransactionDAO> aerospikeAccess = new AerospikeAccess<>(TransactionDAO.class);
        ArrayList<TransactionDAO> allTransactions = aerospikeAccess.getSet();
        if(allTransactions == null)
            throw new DataNotFoundException("There are no transactions in the system.");
        ArrayList<TransactionDAO> ownerTransactions = allTransactions.stream()
                .filter(
                        transactionDAO ->
                                transactionDAO.getBuyer().getUserName().equals(owner.getUserName()) ||
                                transactionDAO.getSeller().getUserName().equals(owner.getUserName()))
                .collect(Collectors.toCollection(ArrayList::new));
        if(ownerTransactions.isEmpty())
            throw new DataNotFoundException("Owner has no transactions.");
        return ownerTransactions;
    }

    public void deletePropertyTransactions(PropertyDAO property){
        AerospikeAccess<TransactionDAO> aerospikeAccess = new AerospikeAccess<>(TransactionDAO.class);
        ArrayList<TransactionDAO> allTransactions = aerospikeAccess.getSet();
        ArrayList<TransactionDAO> propertyTransactions = allTransactions.stream()
                .filter(    
                        transactionDAO ->
                                transactionDAO.getProperty().getPropertyId() == property.getPropertyId()
                )
                .collect(Collectors.toCollection(ArrayList::new));
        aerospikeAccess.deleteRecords(propertyTransactions);
    }

    public void addHypermediaToTransactions(@NotNull ArrayList<TransactionDTO> transactionsDTO, UriInfo uriInfo){
        transactionsDTO.forEach(transactionDTO ->
                {
                    HypermediaAdder.addLink(uriInfo, transactionDTO, "/owners/" + transactionDTO.getBuyer(), "buyer");
                    HypermediaAdder.addLink(uriInfo, transactionDTO, "/owners/" + transactionDTO.getSeller(), "seller");
                    HypermediaAdder.addLink(uriInfo, transactionDTO, "/properties/" + transactionDTO.getProperty(), "property");
                }
        );
    }

    public synchronized TransactionDAO makeTransaction(String buyerID, int propertyID) throws DataNotFoundException, RequestFailedException {
        OwnerDAO buyer = OwnerService.getInstance().getOwner(buyerID);
        PropertyDAO property = PropertyService.getInstance().getProperty(propertyID);
        OwnerDAO seller = OwnerService.getInstance().getOwner(property.getPropertyOwner().getUserName());
        if(property.getForSale() == 0)
            throw new RequestFailedException("Property not for sale.");
        if(buyer.getBalance() < property.getCost())
            throw new RequestFailedException("Insufficient balance.");
        if(buyer.getUserName().equals(seller.getUserName()))
            throw new RequestFailedException("Property is already owned by the buyer.");
        OwnerService.getInstance().addToOwnerBalance(seller, property.getCost());
        OwnerService.getInstance().addToOwnerBalance(buyer, - property.getCost());
        property.setPropertyOwner(buyer);
        property.setForSale(0);
        new AerospikeAccess<>(PropertyDAO.class).updateRecord(property);
        TransactionDAO newTransaction = new TransactionDAO(seller, buyer, property);
        new AerospikeAccess<>(TransactionDAO.class).saveRecord(newTransaction);
        return newTransaction;
    }
}
