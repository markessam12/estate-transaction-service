package com.estate.service;

import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.TransactionDTO;
import com.estate.repository.AerospikeAccess;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * The singleton class representing the transaction service layer and
 * providing all property services to the Controller layer
 */
public interface TransactionService {
    /**
     * Get all the transactions in the database.
     *
     * @return the list of transactions
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<TransactionDAO> getTransactions() throws DataNotFoundException;

    /**
     * Delete all transactions related to a specific owner.
     *
     * @param owner the owner involved in the transactions
     * @throws DataNotFoundException the data not found exception
     */
    public void deleteOwnerTransactions(OwnerDAO owner) throws DataNotFoundException;
    /**
     * Get all transactions involving a specific owner.
     *
     * @param owner the owner involved in the transactions
     * @return the list of owner's transactions
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<TransactionDAO> getOwnerTransactions(OwnerDAO owner) throws DataNotFoundException;

    /**
     * Delete all transactions related to a specific property.
     *
     * @param property the property involved in the transactions
     */
    public void deletePropertyTransactions(PropertyDAO property);

    /**
     * Add hypermedia to transactions.
     *
     * @param transactionsDTO the transactions dto
     * @param uriInfo         the uri info
     */
    public void addHypermediaToTransactions(@NotNull ArrayList<TransactionDTO> transactionsDTO, UriInfo uriInfo);

    /**
     * Transfers ownership of a property from an owner to another and save the data to a new transaction
     *
     * @param buyerID    the buyer id
     * @param propertyID the property id
     * @return the new generated transaction data
     * @throws DataNotFoundException  the data not found exception
     * @throws RequestFailedException the request failed exception which occurs due to a failure in preconditions
     */
    public TransactionDAO makeTransaction(String buyerID, int propertyID) throws DataNotFoundException, RequestFailedException;
}
