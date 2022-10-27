package com.estate.service;

import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.TransactionDTO;
import com.estate.repository.AerospikeAccess;
import jakarta.ws.rs.core.UriInfo;
import jdk.nashorn.internal.objects.Global;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The singleton class representing the transaction service layer and
 * providing all property services to the Controller layer
 */
public class TransactionService {
    private static final TransactionService INSTANCE = new TransactionService();

    private static final Logger logger = LoggerFactory.getLogger(Global.class);

    private TransactionService(){}

    /**
     * Get the singleton instance of the transaction service.
     *
     * @return the transaction service instance
     */
    public static TransactionService getInstance(){
        return INSTANCE;
    }

    /**
     * Get all the transactions in the database.
     *
     * @return the list of transactions
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<TransactionDAO> getTransactions() throws DataNotFoundException {
        AerospikeAccess<TransactionDAO> aerospikeAccess = new AerospikeAccess<>(TransactionDAO.class);
        ArrayList<TransactionDAO> transactions = aerospikeAccess.getSet();
        if(transactions.isEmpty())
            throw new DataNotFoundException("There are no transactions.");
        logger.info("Retrieved all transactions from the database.");
        return transactions;
    }

    /**
     * Delete all transactions related to a specific owner.
     *
     * @param owner the owner involved in the transactions
     * @throws DataNotFoundException the data not found exception
     */
    public void deleteOwnerTransactions(OwnerDAO owner) throws DataNotFoundException{
        ArrayList<TransactionDAO> ownerTransactions = getOwnerTransactions(owner);
        new AerospikeAccess<>(TransactionDAO.class).deleteRecords(ownerTransactions);
        logger.warn("All transactions of owner {} was deleted", owner.getUserName());
    }

    /**
     * Get all transactions involving a specific owner.
     *
     * @param owner the owner involved in the transactions
     * @return the list of owner's transactions
     * @throws DataNotFoundException the data not found exception
     */
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
        logger.warn("Retrieved all the transactions of owner {}", owner.getUserName());
        return ownerTransactions;
    }

    /**
     * Delete all transactions related to a specific property.
     *
     * @param property the property involved in the transactions
     */
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
        logger.warn("All transactions of property with id {} was deleted", property.getPropertyId());
    }

    /**
     * Add hypermedia to transactions.
     *
     * @param transactionsDTO the transactions dto
     * @param uriInfo         the uri info
     */
    public void addHypermediaToTransactions(@NotNull ArrayList<TransactionDTO> transactionsDTO, UriInfo uriInfo) {
        transactionsDTO.forEach(transactionDTO ->
            {
                HypermediaAdder.addLink(uriInfo, transactionDTO, "/owners/" + transactionDTO.getBuyer(), "buyer");
                HypermediaAdder.addLink(uriInfo, transactionDTO, "/owners/" + transactionDTO.getSeller(), "seller");
                HypermediaAdder.addLink(uriInfo, transactionDTO, "/properties/" + transactionDTO.getProperty(), "property");
            }
        );
    }

    /**
     * Transfers ownership of a property from an owner to another and save the data to a new transaction
     *
     * @param buyerID    the buyer id
     * @param propertyID the property id
     * @return the new generated transaction data
     * @throws DataNotFoundException  the data not found exception
     * @throws RequestFailedException the request failed exception which occurs due to a failure in preconditions
     */
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
        logger.warn("Transaction added to database successfully.");
        return newTransaction;
    }
}
