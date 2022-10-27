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
        OwnerServiceImp.getInstance().addOwner("user1", "firstname", "lastname", 100);
        OwnerServiceImp.getInstance().addOwner("user2", "firstname", "lastname", 100);
        OwnerServiceImp.getInstance().addOwner("user3", "firstname", "lastname", 100);
        PropertyServiceImp.getInstance().addProperty("user2","1 cairo", 10);
        PropertyServiceImp.getInstance().addProperty("user3","2 cairo", 10);
        PropertyDAO property1 = PropertyServiceImp.getInstance().getProperty(1);
        property1.setForSale(1);
        PropertyDAO property2 = PropertyServiceImp.getInstance().getProperty(2);
        property2.setForSale(1);
        PropertyServiceImp.getInstance().updateProperty(property1, 1);
        PropertyServiceImp.getInstance().updateProperty(property2, 2);
        TransactionServiceImp.getInstance().makeTransaction("user1",1);
        TransactionServiceImp.getInstance().makeTransaction("user2",2);
    }

    @AfterAll
    static void tearDown() throws DataNotFoundException {
        TransactionServiceImp.getInstance().deletePropertyTransactions(
            PropertyServiceImp.getInstance().getProperty(1)
        );
        assertEquals(1,
                TransactionServiceImp.getInstance().getTransactions().size(),
                "Property Transactions wasn't deleted");
        TransactionServiceImp.getInstance().deleteOwnerTransactions(
                OwnerServiceImp.getInstance().getOwner("user2")
        );
        assertThrows(DataNotFoundException.class, () ->
                TransactionServiceImp.getInstance().getTransactions(),
                "Owner transactions wasn't deleted");
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void getInstance_SingletonService_ReturnSameInstance() {
        TransactionService transactionService = TransactionServiceImp.getInstance();
        assertEquals(transactionService,
                TransactionServiceImp.getInstance(),
                "Transaction service isn't singleton.");
    }

    @Test
    void getTransactions_TwoTransactionInDatabase_True() throws DataNotFoundException {
        assertEquals(2,
                TransactionServiceImp.getInstance().getTransactions().size(),
                "Different transactions count found");
    }

    @Test
    void getOwnerTransactions_OwnerInTwoTransactions_True() throws DataNotFoundException {
        assertEquals(2,
                TransactionServiceImp.getInstance().getOwnerTransactions(
                        OwnerServiceImp.getInstance().getOwner("user2")
                ).size(),
                "Couldn't retrieve the correct user transactions");
    }

    @Test
    void getOwnerTransactions_OwnerInOneTransactions_True() throws DataNotFoundException {
        assertEquals(1,
                TransactionServiceImp.getInstance().getOwnerTransactions(
                        OwnerServiceImp.getInstance().getOwner("user1")
                ).size(),
                "Couldn't retrieve the correct user transactions");
    }
}