package com.estate.service;

import com.estate.exception.DataAlreadyExistsException;
import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
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
class PropertyServiceTest {

    @BeforeAll
    static void setUp() throws DataAlreadyExistsException, DataNotFoundException {
        AerospikeAccess.truncateDatabase();
        OwnerServiceImp.getInstance().addOwner("user1", "firstName", "secondName", 0);
        OwnerServiceImp.getInstance().addOwner("user2", "firstName", "secondName", 0);
        OwnerServiceImp.getInstance().addOwner("user3", "firstName", "secondName", 0);
        PropertyServiceImp.getInstance().addProperty("user1", "1 cairo", 100);
        PropertyServiceImp.getInstance().addProperty("user1", "2 cairo", 200);
        PropertyServiceImp.getInstance().addProperty("user2", "3 cairo", 300);
    }

    @AfterAll
    static void tearDown() {
        AerospikeAccess.truncateDatabase();
    }

    @Test
    void getInstance_SingletonService_ReturnSameInstance() {
        PropertyService propertyService = PropertyServiceImp.getInstance();
        assertEquals(propertyService, PropertyServiceImp.getInstance(),
                "PropertyService isn't singleton");
    }

    @Test
    void getPropertiesOfOwner_OwnsMoreThanOneProperty_True() throws DataNotFoundException {
        assertTrue(
            PropertyServiceImp.getInstance().getPropertiesOfOwner("user1").size() > 1,
            "Owner has more properties then the one's retrieved");
    }

    @Test
    void getProperties_NonExistingOwner_ThrowsDataNotFound(){
        assertThrows(DataNotFoundException.class, () ->
                PropertyServiceImp.getInstance().getPropertiesOfOwner("user3"),
                "Owner's doesn't exist, test should have thrown an exception");
    }

    @Test
    void getPropertiesOfOwner_ExistingOwnerWithNoProperties_ThrowsDataNotFound(){
        assertThrows(DataNotFoundException.class, () ->
                PropertyServiceImp.getInstance().getPropertiesOfOwner("user3"),
                "Owner's doesn't have properties, test should have thrown an exception");
    }

    @Test
    void addProperty_ExistingOwner_SuccessfulAddition() throws DataNotFoundException {
        int ownedPropertiesCountBeforeAddition = PropertyServiceImp.getInstance().getPropertiesOfOwner("user1").size();
        assertDoesNotThrow(() ->
                PropertyServiceImp.getInstance().addProperty("user1", "3 cairo", 300),
                "Property addition throw exception when it shouldn't");
        assertEquals(ownedPropertiesCountBeforeAddition + 1,
            PropertyServiceImp.getInstance().getPropertiesOfOwner("user1").size(),
            "Property wasn't added or couldn't be retrieved");
    }

    @Test
    void getAllProperties_MoreThanOne_True() throws DataNotFoundException {
        assertTrue(PropertyServiceImp.getInstance().getAllProperties().size() > 1,
                "Database should contain more properties than the list retrieved");
    }

    @Test
    void getProperty_Exists_SuccessfulRetrieval(){
        assertDoesNotThrow(() ->
                PropertyServiceImp.getInstance().getProperty(1),
                "Failed to retrieve property using PropertyService");
    }

    @Test
    void getProperty_NonExistingProperty_ThrowDataNotFound(){
        assertThrows(DataNotFoundException.class,
                () -> PropertyServiceImp.getInstance().getProperty(10),
                "Retrieving non existing property should have thrown an exception");
    }

    @Test
    void addAndRetrieveProperty_ExistingOwner_SuccessfulAdditionAndRetrieval() throws DataNotFoundException {
        OwnerDAO owner = OwnerServiceImp.getInstance().getOwner("user2");
        PropertyServiceImp.getInstance().addProperty("user2" ,"address2", 123);
        PropertyDAO property = PropertyServiceImp.getInstance().getProperty(
            PropertyServiceImp.getInstance().generateUniquePropertyId() - 1
        );
        assertTrue(property.getAddress().equals("address2") &&
                property.getCost() == 123 &&
                property.getPropertyOwner().getUserName().equals("user2"),
                "Details of the retrieved property aren't correct");
    }

    @Test
    void deleteProperty_ExistingProperty_SuccessfulDeletion() throws DataNotFoundException {
        assertDoesNotThrow(() -> PropertyServiceImp.getInstance().deleteProperty(3),
            "Failed to delete an existing property");
        assertThrows(DataNotFoundException.class,
                () -> PropertyServiceImp.getInstance().getProperty(3),
                "Failed to delete the property");
    }

    @Test
    void updateProperty_ExistingProperty_SuccessfulUpdate() throws DataNotFoundException {
        PropertyDAO updatedProperty = PropertyServiceImp.getInstance().getProperty(2);
        updatedProperty.setCost(123);
        updatedProperty.setForSale(1);
        PropertyDAO retrievedProperty = PropertyServiceImp.getInstance().updateProperty(updatedProperty, 2);
        assertTrue(updatedProperty.getCost() == retrievedProperty.getCost() &&
                updatedProperty.getForSale() == retrievedProperty.getForSale(),
                "Failed to update the property details");
    }
}