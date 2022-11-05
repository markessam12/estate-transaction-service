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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The singleton class representing the transaction service layer and
 * providing all property services to the Controller layer
 */
public class TransactionServiceImp implements TransactionService{
    private static final TransactionServiceImp INSTANCE = new TransactionServiceImp();

    private static final Logger logger
        = LoggerFactory.getLogger(TransactionServiceImp.class);

    private TransactionServiceImp(){}

    /**
     * Get the singleton instance of the transaction service.
     *
     * @return the transaction service instance
     */
    public static TransactionServiceImp getInstance(){
        return INSTANCE;
    }

    /**
     * Get all the transactions in the database.
     *
     * @return the list of transactions
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<TransactionDAO> getTransactions() throws DataNotFoundException {
        logger.info("Retrieving all transactions.");
        AerospikeAccess<TransactionDAO> aerospikeAccess = new AerospikeAccess<>(TransactionDAO.class);
        ArrayList<TransactionDAO> transactions = aerospikeAccess.getSet();
        if(transactions.isEmpty()) {
            logger.info("No transactions found in the database");
            throw new DataNotFoundException("There are no transactions.");
        }
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
        logger.warn("Deleting all transactions of owner {}", owner.getUserName());
        new AerospikeAccess<>(TransactionDAO.class).deleteRecords(ownerTransactions);
    }

    /**
     * Get all transactions involving a specific owner.
     *
     * @param owner the owner involved in the transactions
     * @return the list of owner's transactions
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<TransactionDAO> getOwnerTransactions(OwnerDAO owner) throws DataNotFoundException {
        logger.info("Retrieving all transactions of owner {}", owner.getUserName());
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
        if(ownerTransactions.isEmpty()) {
            logger.info("owner has no transactions");
            throw new DataNotFoundException("Owner has no transactions.");
        }
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
        logger.warn("Deleting all transactions of property {}", property.getPropertyId());
        aerospikeAccess.deleteRecords(propertyTransactions);
    }

    /**
     * Add hypermedia to transactions.
     *
     * @param transactionsDTO the transactions dto
     * @param uriInfo         the uri info
     */
    public void addHypermediaToTransactions(@NotNull ArrayList<TransactionDTO> transactionsDTO, UriInfo uriInfo){
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
        OwnerDAO buyer = OwnerServiceImp.getInstance().getOwner(buyerID);
        PropertyDAO property = PropertyServiceImp.getInstance().getProperty(propertyID);
        OwnerDAO seller = OwnerServiceImp.getInstance().getOwner(property.getPropertyOwner().getUserName());
        if(property.getForSale() == 0) {
            logger.warn("Transaction failed as property isn't for sale");
            throw new RequestFailedException("Property not for sale.");
        }
        if(buyer.getBalance() < property.getCost()) {
            logger.warn("Transaction failed as owner balance not sufficient");
            throw new RequestFailedException("Insufficient balance.");
        }
        if(buyer.getUserName().equals(seller.getUserName())) {
            logger.warn("Transaction failed as property already owned");
            throw new RequestFailedException("Property is already owned by the buyer.");
        }
        OwnerServiceImp.getInstance().addToOwnerBalance(seller, property.getCost());
        OwnerServiceImp.getInstance().addToOwnerBalance(buyer, - property.getCost());
        property.setPropertyOwner(buyer);
        property.setForSale(0);
        new AerospikeAccess<>(PropertyDAO.class).updateRecord(property);
        TransactionDAO newTransaction = new TransactionDAO(seller, buyer, property);
        logger.info("Transaction {} created successfully! Buyer {}, Seller {}, property {}, price {} ",
            newTransaction.getDate(), buyerID, seller.getUserName(), propertyID, property.getCost());
        new AerospikeAccess<>(TransactionDAO.class).saveRecord(newTransaction);
        return newTransaction;
    }
}
