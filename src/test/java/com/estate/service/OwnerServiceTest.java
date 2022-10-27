package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.OwnerDAO;
import com.estate.repository.AerospikeAccess;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test Naming Convention used is the first one in this <a href="https://dzone.com/articles/7-popular-unit-test-naming">website</a>
 * Please take note of the following best practices when making unit tests:
 * 1. Tests shouldn't have try/catch blocks
 * 2. Each test should contain only 1 assertion
 * 3. Tests shouldn't depend on each others nor have a specific order
 */
class OwnerServiceTest {
    @BeforeAll
    static void setUp() throws DataAlreadyExistsException {
        AerospikeAccess.truncateDatabase();
        OwnerServiceImp.getInstance().addOwner("user1", "firstName", "secondName", 0);
    }

    @AfterAll
    static void tearDown() {
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void getInstance_SingletonClass_ReturnSameInstance() {
        OwnerService ownerService = OwnerServiceImp.getInstance();
        assertEquals(ownerService, OwnerServiceImp.getInstance(),
                "owner service should be singleton");
    }

    @Test
    void addOwner_NewOwner_SuccessfulOwnerAddition()
        throws DataNotFoundException, DataAlreadyExistsException {
        String username = "user2";
        OwnerServiceImp.getInstance().addOwner(username, "firstName2", "lastName2",1000);
        OwnerDAO retrieved = OwnerServiceImp.getInstance().getOwner(username);
        assertEquals(username, retrieved.getUserName(),
                "Saved owner has different username");
    }

    @Test
    void getAllOwners_MoreThanOneOwnerExists_True()
        throws DataNotFoundException, DataAlreadyExistsException {
        OwnerServiceImp.getInstance().addOwner("user3", "firstName3", "lastName3",1000);
        assertTrue(OwnerServiceImp.getInstance().getAllOwners().size() > 1,
                    "Owner service didn't retrieve the correct number of owners");
    }


    @Test
    void addBalance_OwnerExists_SuccessfulBalanceAddition() throws DataNotFoundException {
        assertDoesNotThrow(()-> {
            OwnerDAO owner = OwnerServiceImp.getInstance().getOwner("user1");
            OwnerServiceImp.getInstance().addToOwnerBalance(owner, 2000);},
            "Owner should be on database but couldn't be retrieved or edited");
        assertEquals(
            2000,
            OwnerServiceImp.getInstance().getOwner("user1").getBalance(),
            "Balance failed to update");
    }

    @Test
    void changeOwnerData_OwnerExists_OnlyNameIsUpdated() throws DataNotFoundException {
        assertDoesNotThrow(() -> {
            OwnerDAO updated = OwnerServiceImp.getInstance().getOwner("user1");
            updated.setUserName("newUserName1");
            updated.setFirstName("newFirstName");
            updated.setLastName("newLastName");
            updated.setBalance(999);
            OwnerServiceImp.getInstance().updateOwner(updated, "user1");},
            "Failed to get or updated an existing owner");
        OwnerDAO retrieved = OwnerServiceImp.getInstance().getOwner("user1");
        assertTrue(
            retrieved.getFirstName().equals("newFirstName") &&
            retrieved.getLastName().equals("newLastName") &&
            retrieved.getBalance() != 999,
            "Owner wasn't updated correctly.");
    }

    @Test()
    void deleteOwner_NonExistingOwner_ThrowsDataNotFound(){
        assertThrowsExactly(DataNotFoundException.class, () -> {
            OwnerServiceImp.getInstance().deleteOwner("user12");},
            "Deleting a non-existing owner should throw exception");
    }

    @Test()
    void addOwner_ExistingOwner_ThrowDataAlreadyExists(){
        assertThrows(DataAlreadyExistsException.class, () -> {
            OwnerServiceImp.getInstance().addOwner("user1", "firstName", "secondName", 0);},
            "Adding an existing owner should throw an exception");
    }

    @Test()
    void updateOwner_NonExistingOwner_ThrowDataNotFound(){
        OwnerDAO updated = new OwnerDAO("user12","newFirstName","newLastName",2000);
        assertThrows(DataNotFoundException.class, () -> {
            OwnerServiceImp.getInstance().updateOwner(updated, "user12");},
            "Updating non existing owner should throw an exception");
    }

    @Test
    void deleteOwner_ExistingOwner_SuccessfulDeletion() throws DataAlreadyExistsException {
        OwnerServiceImp.getInstance().addOwner("userX","firstName", "lastName", 100000);
        assertDoesNotThrow(() -> {
            OwnerServiceImp.getInstance().deleteOwner("userX");},
            "Deleting an existing owner shouldn't throw and exception");
        assertThrowsExactly(DataNotFoundException.class, () ->
            OwnerServiceImp.getInstance().getOwner("userX"));
    }
}