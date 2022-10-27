package com.estate.repository;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class AerospikeAccessTest {
    OwnerDAO ownerStub = new OwnerDAO("markessam12", "Mark", "Essam", 2500);
    PropertyDAO propertyStub = new PropertyDAO(1, "12 cairo egypt", ownerStub, 1235);
    AerospikeAccess<OwnerDAO> aerospikeOwnerAccessStub = new AerospikeAccess<>(OwnerDAO.class);
    AerospikeAccess<PropertyDAO> aerospikePropertyAccessStub = new AerospikeAccess<>(PropertyDAO.class);

    @BeforeAll
    static void setUp() {
        AerospikeAccess.truncateDatabase();
    }

    @AfterAll
    static void tearDown() {
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void Test01_saveAndReadOwner() {
        aerospikeOwnerAccessStub.saveRecord(ownerStub);
        OwnerDAO retrieved = aerospikeOwnerAccessStub.getRecord("markessam12");
        assertEquals(ownerStub.getUserName(),retrieved.getUserName(),"owner username saved in database is incorrect");
        assertEquals(ownerStub.getFirstName(),retrieved.getFirstName(),"owner firstname saved in database is incorrect");
        assertEquals(ownerStub.getLastName(),retrieved.getLastName(),"owner lastname saved in database is incorrect");
        assertEquals(ownerStub.getBalance(),retrieved.getBalance(),"owner balance saved in database is incorrect");
    }

    @Test
    void Test02_saveAndReadProperty() {
        aerospikePropertyAccessStub.saveRecord(propertyStub);
        PropertyDAO retrieved = aerospikePropertyAccessStub.getRecord(1);
        assertEquals(propertyStub.getPropertyId(),retrieved.getPropertyId(),"property id saved in database is incorrect");
        assertEquals(propertyStub.getAddress(),retrieved.getAddress(),"property address saved in database is incorrect");
        assertEquals(propertyStub.getCost(),retrieved.getCost(),"property cost saved in database is incorrect");
        assertEquals(ownerStub.getUserName(),retrieved.getPropertyOwner().getUserName(),"property owner saved in database is incorrect");
    }

    @Test
    void Test03_updateOwnerRecord() {
        ownerStub.setFirstName("Marko");
        ownerStub.setLastName("Eso");
        ownerStub.setBalance(3150);
        aerospikeOwnerAccessStub.updateRecord(ownerStub);
        OwnerDAO retrivedUpdatedOwner = aerospikeOwnerAccessStub.getRecord(ownerStub.getUserName());
        assertEquals(ownerStub.getBalance(),retrivedUpdatedOwner.getBalance(),"owner balance wasn't changed in database");
        assertEquals(ownerStub.getFirstName(),retrivedUpdatedOwner.getFirstName(),"owner firstname wasn't changed in database");
        assertEquals(ownerStub.getLastName(),retrivedUpdatedOwner.getLastName(),"owner lastname wasn't changed in database");
    }

    @Test
    void Test04_addExtraUserAndGetSet() {
        ownerStub.setUserName("mark12");
        aerospikeOwnerAccessStub.saveRecord(ownerStub);
        assertEquals(2, aerospikeOwnerAccessStub.getSet().size(), "Incorrect set size");
        assertEquals(OwnerDAO.class, aerospikeOwnerAccessStub.getSet().get(0).getClass(), "incorrect set datatype");
    }

    @Test
    void Test05_deletePropertyRecord() {
        aerospikePropertyAccessStub.deleteRecord(propertyStub);
        assertNull(aerospikeOwnerAccessStub.getRecord(propertyStub.getPropertyId()),"failed to delete record from the database");
    }

    @Test
    void Test06_deleteAllRecords(){
        aerospikeOwnerAccessStub.deleteRecords(
                aerospikeOwnerAccessStub.getSet()
        );
        assertTrue(aerospikeOwnerAccessStub.getSet().isEmpty(), "failed to delete the set records");
    }
}