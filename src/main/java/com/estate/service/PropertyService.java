package com.estate.service;

import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.repository.AerospikeAccess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import jdk.nashorn.internal.objects.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The singleton class representing the property service layer and
 * providing all property services to the Controller layer
 */
public class PropertyService {
    private static final PropertyService INSTANCE = new PropertyService();

    private static final Logger logger
        = LoggerFactory.getLogger(Global.class);

    private PropertyService(){}

    /**
     * Get the singleton instance of the property service.
     *
     * @return the property service instance
     */
    public static PropertyService getInstance(){
        return INSTANCE;
    }

    /**
     * Gets all properties of a specific owner.
     *
     * @param userName the username
     * @return the list of properties of owner
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<PropertyDAO> getPropertiesOfOwner(String userName) throws DataNotFoundException {
        ArrayList<PropertyDAO> properties = getAllProperties();
        ArrayList<PropertyDAO> ownerProperties = properties.stream()
                .filter(propertyDAO -> propertyDAO.getPropertyOwner().getUserName().equals(userName))
                .collect(Collectors.toCollection(ArrayList::new));
        if(ownerProperties.isEmpty())
            throw new DataNotFoundException("Owner has no properties.");
        logger.info("Retrieve all properties of owner {}", userName);
        return ownerProperties;
    }

    /**
     * Gets all properties in the database.
     *
     * @return the list of properties
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<PropertyDAO> getAllProperties() throws DataNotFoundException{
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        ArrayList<PropertyDAO> properties = aerospikeAccess.getSet();
        if(properties.isEmpty())
            throw new DataNotFoundException("There is no records for any property in the database.");
        logger.info("Retrieved the all properties in the database");
        return properties;
    }

    /**
     * Gets a specific property from the database.
     *
     * @param propertyId he unique property id
     * @return the retrieved property
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO getProperty(int propertyId) throws DataNotFoundException{
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
        if(property == null){
            logger.warn("Trying to access non existing property of id {}", propertyId);
            throw new DataNotFoundException("Property not found!");
        }
        logger.info("Retrieved the property of id {}", propertyId);
        return property;
    }

    /**
     * Delete a specific property from the database.
     *
     * @param propertyId the unique property id
     * @return the deleted property
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO deleteProperty(int propertyId) throws DataNotFoundException{
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
        if(property == null)
            throw new DataNotFoundException("Property to delete doesn't exist.");
        TransactionService.getInstance().deletePropertyTransactions(property);
        aerospikeAccess.deleteRecord(property);
        logger.info("Property of id {} was deleted", propertyId);
        return property;
    }

    /**
     * Update a specific property in the database.
     *
     * @param propertyUpdated the updated property data sent from controller layer
     * @param id              the unique property id
     * @return the updated property data retrieved from database
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO updateProperty(PropertyDAO propertyUpdated, int id) throws DataNotFoundException {
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(id);
        if(property == null){
            logger.warn(" User tried to update non existing property of id {}", id);
            throw new DataNotFoundException("Property not found!");
        }
        property.setCost(propertyUpdated.getCost());
        property.setForSale(propertyUpdated.getForSale());
        aerospikeAccess.updateRecord(property);
        logger.info("Updated cost and sale state of the property with id {}", property.getPropertyId());
        return property;
    }

    /**
     * Add a new property to the database.
     *
     * @param ownerUserName   the owner username
     * @param propertyAddress the property address
     * @param cost            the cost
     * @return the new property data
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO addProperty(String ownerUserName, String propertyAddress, long cost) throws DataNotFoundException{
        OwnerDAO owner = OwnerService.getInstance().getOwner(ownerUserName);
        logger.info("Generating a new property Id");
        int newId = generateUniquePropertyId();
        PropertyDAO newProperty = new PropertyDAO(newId, propertyAddress, owner, cost);
        new AerospikeAccess<>(PropertyDAO.class).saveRecord(newProperty);
        logger.info("New property of id {} was added to the database", newId);
        return newProperty;
    }

    /**
     * Generate new unique property id.
     *
     * @return the new id of type int
     */
    public int generateUniquePropertyId(){
        AerospikeAccess<PropertyDAO> aerospikePropertyAccess = new AerospikeAccess<>(PropertyDAO.class);
        Optional<PropertyDAO> propertyWithHighestID =  aerospikePropertyAccess.getSet()
            .stream()
            .max(Comparator.comparing(PropertyDAO::getPropertyId));
        int highestID = 0;
        if(propertyWithHighestID.isPresent())
            highestID = propertyWithHighestID.get().getPropertyId();
        return highestID + 1;
    }

}
