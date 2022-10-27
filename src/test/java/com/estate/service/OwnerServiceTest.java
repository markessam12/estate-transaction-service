package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.OwnerDAO;
import com.estate.repository.AerospikeAccess;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

//There is a bad practice here as tests shouldn't depend on each others nor have more than one assertion
@TestMethodOrder(MethodOrderer.MethodName.class)
class OwnerServiceTest {
    OwnerDAO owner;

    @BeforeAll
    static void setUp() throws DataAlreadyExistsException {
        AerospikeAccess.truncateDatabase();
        OwnerServiceImp.getInstance().addOwner("user1", "firstName", "secondName", 0);
    }

    @AfterAll
    static void tearDown() {
        AerospikeAccess.truncateDatabase();
    }

    @BeforeEach
    void setOwner() throws DataNotFoundException {
        this.owner = OwnerServiceImp.getInstance().getOwner("user1");
    }

    @Test
    void Test01_serviceShouldBeSingleton() {
        OwnerService ownerService = OwnerServiceImp.getInstance();
        assertEquals(ownerService, OwnerServiceImp.getInstance(),
                "owner service should be singleton");
    }

    @Test
    void Test02_addAndRetrieveOwnerUsingOwnerService() throws DataNotFoundException {
        String username = "user1";
        this.owner = OwnerServiceImp.getInstance().getOwner(username);
        assertEquals(username, owner.getUserName(),
                "Saved owner has different username");
    }

    @Test
    void Test03_getAllOwnersUsingOwnerService() throws DataNotFoundException {
            assertEquals(1, OwnerServiceImp.getInstance().getAllOwners().size(),
                    "Owner service didn't retrieve the correct number of owners");
    }


    @Test
    void Test04_addBalanceToOwner(){
        assertDoesNotThrow(()-> {
            OwnerServiceImp.getInstance().addToOwnerBalance(owner, 2000);
        });
    }

    @Test
    void Test05_changeOwnerFirstAndLastNames(){
        assertDoesNotThrow(() -> {
            owner.setFirstName("newFirstName");
            owner.setLastName("newLastName");
            OwnerServiceImp.getInstance().updateOwner(owner, "user1");
        });
    }

    @Test
    void Test06_ownerDataShouldBeUpdated(){
        assertTrue(
                owner.getBalance() == 2000 &&
                        owner.getFirstName().equals("newFirstName") &&
                        owner.getLastName().equals("newLastName")
        );
    }

    @Test()
    void Test07_deleteNonExistingOwnerUsingOwnerService(){
        assertThrowsExactly(DataNotFoundException.class, () -> {
            OwnerServiceImp.getInstance().deleteOwner("user2");
        });
    }

    @Test()
    void Test08_addExistingOwnerUsingOwnerService(){
        assertThrows(DataAlreadyExistsException.class, () -> {
            OwnerServiceImp.getInstance().addOwner("user1", "firstName", "secondName", 0);
        });
    }

    @Test()
    void Test09_updateNonExistingOwnerUsingOwnerService(){
        owner.setBalance(6150);
        assertThrows(DataNotFoundException.class, () -> {
            OwnerServiceImp.getInstance().updateOwner(owner, "user2");
        });
    }

    @Test
    void Test10_deleteOwnerUsingOwnerService(){
        assertDoesNotThrow(() -> {
            OwnerServiceImp.getInstance().deleteOwner("user1");
        });
    }
}