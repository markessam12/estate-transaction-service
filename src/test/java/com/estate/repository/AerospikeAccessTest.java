package com.estate.repository;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test Naming Convention used is the first one in this <a href="https://dzone.com/articles/7-popular-unit-test-naming">website</a>
 * Please take note of the following best practices when making unit tests:
 * 1. Tests shouldn't have try/catch blocks
 * 2. Each test should contain only 1 assertion
 * 3. Tests shouldn't depend on each others nor have a specific order
 */
class AerospikeAccessTest {
    static AerospikeAccess<OwnerDAO> aerospikeOwnerAccessStub = new AerospikeAccess<>(OwnerDAO.class);
    static AerospikeAccess<PropertyDAO> aerospikePropertyAccessStub = new AerospikeAccess<>(PropertyDAO.class);

    @BeforeAll
    static void setUp() {
        AerospikeAccess.truncateDatabase();
        OwnerDAO owner = new OwnerDAO("user1", "firstName", "lastName", 200);
        aerospikeOwnerAccessStub.saveRecord(owner);
        aerospikePropertyAccessStub.saveRecord(
            new PropertyDAO(1, "address1", owner, 100)
        );
    }

    @AfterAll
    static void tearDown() {
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void saveAndReadOwner_NewOwner_ConsistentData() {
        OwnerDAO saved, retrieved;
        saved = new OwnerDAO("user2", "firstName2", "lastName2", 100);
        aerospikeOwnerAccessStub.saveRecord(saved);
        retrieved = aerospikeOwnerAccessStub.getRecord(saved.getUserName());
        assertTrue(saved.getUserName().equals(retrieved.getUserName()) &&
                saved.getFirstName().equals(retrieved.getFirstName()) &&
                saved.getLastName().equals(retrieved.getLastName()) &&
                saved.getBalance() == retrieved.getBalance(),
                "owner data saved then retrieved is not consistent");
    }

    @Test
    void saveAndReadProperty_NewProperty_ConsistentData() {
        OwnerDAO owner = aerospikeOwnerAccessStub.getRecord("user1");
        PropertyDAO saved = new PropertyDAO(2,"address2", owner,100);
        aerospikePropertyAccessStub.saveRecord(saved);
        PropertyDAO retrieved = aerospikePropertyAccessStub.getRecord(2);
        assertTrue(
            saved.getPropertyId() == retrieved.getPropertyId() &&
            saved.getAddress().equals(retrieved.getAddress()) &&
            saved.getCost() == retrieved.getCost() &&
            saved.getPropertyOwner().getUserName().equals(retrieved.getPropertyOwner().getUserName()),
            "property data saved then retrieved is not consistent");
    }

    @Test
    void updateOwner_OwnerExists_SuccessUpdate() {
        OwnerDAO updated =  aerospikeOwnerAccessStub.getRecord("user1");
        updated.setBalance(1000);
        updated.setFirstName("newFirstName");
        updated.setLastName("newLastName");
        aerospikeOwnerAccessStub.updateRecord(updated);
        OwnerDAO retrieved = aerospikeOwnerAccessStub.getRecord(updated.getUserName());
        assertTrue(
            updated.getBalance() == retrieved.getBalance() &&
            updated.getFirstName().equals(retrieved.getFirstName()) &&
            updated.getLastName().equals(retrieved.getLastName()),
            "owner update wasn't reflected on database");
    }

    @Test
    void getOwnersSet_MoreThanOneOwner_True() {
        aerospikeOwnerAccessStub.saveRecord(
            new OwnerDAO("user3", "firstName3", "lastName3", 1000)
        );
        assertTrue(
            aerospikeOwnerAccessStub.getSet().size() >= 2  &&
                OwnerDAO.class == aerospikeOwnerAccessStub.getSet().get(0).getClass(),
            "incorrect set retrieved");
    }

    @Test
    void deleteProperty_PropertyExist_DeletedSuccessfully() {
        aerospikePropertyAccessStub.deleteRecord(
            aerospikePropertyAccessStub.getRecord(1)
        );
        assertNull(aerospikeOwnerAccessStub.getRecord(1),
            "failed to delete record from the database");
    }
}