package com.estate.repository;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AerospikeTransactionProcessor {
    public void deleteTransactionForObject(Object mainDeleted){
        //Cascade constrain that automatically deletes transactions
        //related to the deleted object
        if(mainDeleted.getClass() == OwnerDAO.class)
            deleteTransactionList(getTransactionsOfOwner(((OwnerDAO) mainDeleted).getUserName()));
        if(mainDeleted.getClass() == PropertyDAO.class)
            deleteTransactionList(getTransactionsOfProperty(((PropertyDAO) mainDeleted).getPropertyId()));
    }

    public void deleteTransactionList(List<TransactionDAO> transactions){
        if(!transactions.isEmpty())
            for(TransactionDAO transaction : transactions){
                AerospikeAccess.mapper.delete(transaction);
            }
    }

    public List<TransactionDAO> getTransactionsOfOwner(String id){
        ArrayList<TransactionDAO> transactions =  new AerospikeAccess<>(TransactionDAO.class)
                .getSet();
        return transactions
                .stream()
                .filter(
                        transactionDAO ->
                                transactionDAO.getBuyer().getUserName().equals(id) ||
                                        transactionDAO.getSeller().getUserName().equals(id)
                )
                .collect(Collectors.toList());
    }

    public List<TransactionDAO> getTransactionsOfProperty(int propertyId){
        ArrayList<TransactionDAO> transactions =  new AerospikeAccess<>(TransactionDAO.class)
                .getSet();
        return transactions
                .stream()
                .filter(transactionDAO -> transactionDAO.getProperty().getPropertyId() == propertyId)
                .collect(Collectors.toList());
    }
}
