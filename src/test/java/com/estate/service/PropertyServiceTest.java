package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.PropertyDAO;
import com.estate.repository.AerospikeAccess;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class PropertyServiceTest {

    @BeforeAll
    static void setUp() throws DataAlreadyExistsException, DataNotFoundException {
        AerospikeAccess.truncateDatabase();
        OwnerServiceImp.getInstance().addOwner("user1", "firstName", "secondName", 0);
        OwnerServiceImp.getInstance().addOwner("user2", "firstName", "secondName", 0);
        PropertyServiceImp.getInstance().addProperty("user1", "1 cairo", 100);
        PropertyServiceImp.getInstance().addProperty("user1", "2 cairo", 200);
    }

    @AfterAll
    static void tearDown() {
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void Test01_propertyServiceShouldBeSingleton() {
        PropertyService propertyService = PropertyServiceImp.getInstance();
        assertEquals(propertyService, PropertyServiceImp.getInstance(),
                "PropertyService isn't singleton");
    }

    @Test
    void Test02_getPropertiesOfOwnerUsingService() throws DataNotFoundException {
        assertEquals(
                2,
            PropertyServiceImp.getInstance().getPropertiesOfOwner("user1").size(),
                "Owner has more properties then the one's retrieved");
    }

    @Test
    void Test03_getPropertiesOfNonExistingOwnerUsingService(){
        assertThrows(DataNotFoundException.class, () ->
                PropertyServiceImp.getInstance().getPropertiesOfOwner("user3"),
                "Owner's doesn't exist, test should have thrown an exception");
    }

    @Test
    void Test04_getPropertiesOfOwnerThatHasNoPropertiesUsingService(){
        assertThrows(DataNotFoundException.class, () ->
                PropertyServiceImp.getInstance().getPropertiesOfOwner("user2"),
                "Owner's doesn't have properties, test should have thrown an exception");
    }

    @Test
    void Test05_addPropertyUsingService() {
        assertDoesNotThrow(() ->
                PropertyServiceImp.getInstance().addProperty("user2", "3 cairo", 300),
                "Property addition throw exception when it shouldn't");
    }

    @Test
    void Test05_getAllPropertiesUsingService() throws DataNotFoundException {
        assertEquals(
                3,
            PropertyServiceImp.getInstance().getAllProperties().size(),
                "Owner has more properties then the one's retrieved");
    }

    @Test
    void Test06_getPropertyUsingService(){
        assertDoesNotThrow(() ->
                PropertyServiceImp.getInstance().getProperty(1),
                "Failed to retrieve property using PropertyService");
    }

    @Test
    void Test07_getNonExistingPropertyUsingService(){
        assertThrows(DataNotFoundException.class,
                () -> PropertyServiceImp.getInstance().getProperty(10),
                "Retrieving non existing property should have thrown an exception");
    }

    @Test
    void Test08_checkRetrievedPropertyDetails() throws DataNotFoundException {
        PropertyDAO property = PropertyServiceImp.getInstance().getProperty(1);
        assertTrue(property.getAddress().equals("1 cairo") &&
                property.getCost() == 100,
                "Details of the retrieved property aren't correct");
    }

    @Test
    void Test09_deletePropertyUsingService() throws DataNotFoundException {
        PropertyServiceImp.getInstance().deleteProperty(1);
        assertThrows(DataNotFoundException.class,
                () -> PropertyServiceImp.getInstance().getProperty(1),
                "Failed to delete the property");
    }

    @Test
    void Test10_updatePropertyUsingService() throws DataNotFoundException {
        PropertyDAO updatedProperty = PropertyServiceImp.getInstance().getProperty(2);
        updatedProperty.setCost(123);
        updatedProperty.setForSale(1);
        PropertyDAO retrievedProperty = PropertyServiceImp.getInstance().updateProperty(updatedProperty, 2);
        assertTrue(updatedProperty.getCost() == retrievedProperty.getCost() &&
                updatedProperty.getForSale() == retrievedProperty.getForSale(),
                "Failed to update the property details");
    }
}