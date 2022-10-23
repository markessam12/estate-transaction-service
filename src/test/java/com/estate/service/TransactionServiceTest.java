package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.TransactionMapper;
import com.estate.repository.AerospikeAccess;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    @BeforeAll
    static void setup() throws DataAlreadyExistsException, DataNotFoundException, RequestFailedException {
        AerospikeAccess.truncateDatabase();
        //prepare data inside the database
        OwnerService.getInstance().addOwner("user1", "firstname", "lastname", 100);
        OwnerService.getInstance().addOwner("user2", "firstname", "lastname", 100);
        OwnerService.getInstance().addOwner("user3", "firstname", "lastname", 100);
        PropertyService.getInstance().addProperty("user2","1 cairo", 10);
        PropertyService.getInstance().addProperty("user3","2 cairo", 10);
        PropertyDAO property1 = PropertyService.getInstance().getProperty(1);
        property1.setForSale(1);
        PropertyDAO property2 = PropertyService.getInstance().getProperty(2);
        property2.setForSale(1);
        PropertyService.getInstance().updateProperty(property1, 1);
        PropertyService.getInstance().updateProperty(property2, 2);
        TransactionService.getInstance().makeTransaction("user1",1);
        TransactionService.getInstance().makeTransaction("user2",2);
    }

    @AfterAll
    static void tearDown() throws DataNotFoundException {
        TransactionService.getInstance().deletePropertyTransactions(
                PropertyService.getInstance().getProperty(1)
        );
        assertEquals(1,
                TransactionService.getInstance().getTransactions().size(),
                "Property Transactions wasn't deleted");
        TransactionService.getInstance().deleteOwnerTransactions(
                OwnerService.getInstance().getOwner("user2")
        );
        assertThrows(DataNotFoundException.class, () ->
                TransactionService.getInstance().getTransactions(),
                "Owner transactions wasn't deleted");
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void getInstance_SingletonService_ReturnSameInstance() {
        TransactionService transactionService = TransactionService.getInstance();
        assertEquals(transactionService,
                TransactionService.getInstance(),
                "Transaction service isn't singleton.");
    }

    @Test
    void getTransactions_TwoTransactionInDatabase_True() throws DataNotFoundException {
        assertEquals(2,
                TransactionService.getInstance().getTransactions().size(),
                "Different transactions count found");
    }

    @Test
    void getOwnerTransactions_OwnerInTwoTransactions_True() throws DataNotFoundException {
        assertEquals(2,
                TransactionService.getInstance().getOwnerTransactions(
                        OwnerService.getInstance().getOwner("user2")
                ).size(),
                "Couldn't retrieve the correct user transactions");
    }

    @Test
    void getOwnerTransactions_OwnerInOneTransactions_True() throws DataNotFoundException {
        assertEquals(1,
                TransactionService.getInstance().getOwnerTransactions(
                        OwnerService.getInstance().getOwner("user1")
                ).size(),
                "Couldn't retrieve the correct user transactions");
    }
}