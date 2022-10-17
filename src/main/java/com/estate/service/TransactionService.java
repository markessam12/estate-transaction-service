package com.estate.service;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.TransactionMapper;
import com.estate.repository.AerospikeAccess;
import com.estate.repository.HypermediaCreator;
import jakarta.ws.rs.core.UriInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransactionService {
    private static TransactionService INSTANCE = new TransactionService();

    private TransactionService(){}

    public static TransactionService getInstance(){
        return INSTANCE;
    }

    public ArrayList<TransactionDAO> getTransactions(){
        AerospikeAccess<TransactionDAO> aerospikeAccess = new AerospikeAccess<>(TransactionDAO.class);
        return aerospikeAccess.getSet();
    }

    public void deleteOwnerTransactions(OwnerDAO owner){
        ArrayList<TransactionDAO> ownerTransactions = getOwnerTransactions(owner);
        new AerospikeAccess<TransactionDAO>(TransactionDAO.class).deleteRecords(ownerTransactions);
    }

    public ArrayList<TransactionDAO> getOwnerTransactions(OwnerDAO owner){
        AerospikeAccess<TransactionDAO> aerospikeAccess = new AerospikeAccess<>(TransactionDAO.class);
        ArrayList<TransactionDAO> allTransactions = aerospikeAccess.getSet();
        ArrayList<TransactionDAO> ownerTransactions = allTransactions.stream()
                .filter(
                        transactionDAO ->
                                transactionDAO.getBuyer().getUserName().equals(owner.getUserName()) ||
                                        transactionDAO.getSeller().getUserName().equals(owner.getUserName())
                )
                .collect(Collectors.toCollection(ArrayList::new));
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

    public ArrayList<TransactionDTO> addHypermediaToTransactions(@NotNull ArrayList<TransactionDTO> transactions, UriInfo uriInfo){
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        transactions.forEach(
                transactionDTO -> transactionDTO.setLinks(
                        hypermediaCreator.baseUri("/owners/" + transactionDTO.getBuyer(), "buyer")
                                .baseUri("/owners/" + transactionDTO.getSeller(), "seller")
                                .baseUri("/properties/" + transactionDTO.getProperty(), "property")
                                .build()
                )
        );
        return transactions;
    }
}
