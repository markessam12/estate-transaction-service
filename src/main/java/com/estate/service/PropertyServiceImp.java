package com.estate.service;

import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.repository.AerospikeAccess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The singleton class representing the property service layer and
 * providing all property services to the Controller layer
 */
public class PropertyServiceImp implements PropertyService{
    private static final PropertyServiceImp INSTANCE = new PropertyServiceImp();

    private static final Logger logger
        = LoggerFactory.getLogger(PropertyServiceImp.class);

    private PropertyServiceImp(){}

    /**
     * Get the singleton instance of the property service.
     *
     * @return the property service instance
     */
    public static PropertyServiceImp getInstance(){
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
        logger.info("Retrieving all properties of owner {}", userName);
        ArrayList<PropertyDAO> ownerProperties = properties.stream()
                .filter(propertyDAO -> propertyDAO.getPropertyOwner().getUserName().equals(userName))
                .collect(Collectors.toCollection(ArrayList::new));
        if(ownerProperties.isEmpty()){
            throw new DataNotFoundException("Owner has no properties.");
        }
        return ownerProperties;
    }

    /**
     * Gets all properties in the database.
     *
     * @return the list of properties
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<PropertyDAO> getAllProperties() throws DataNotFoundException{
        logger.info("Retrieving all properties in the database.");
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        ArrayList<PropertyDAO> properties = aerospikeAccess.getSet();
        if(properties.isEmpty())
            throw new DataNotFoundException("There is no records for any property in the database.");
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
        logger.info("Retrieving property {}", propertyId);
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
        if(property == null){
            logger.warn("Failed to retrieve property {}, property not found!", propertyId);
            throw new DataNotFoundException("Property not found!");
        }
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
        logger.info("Attempting to delete property {}", propertyId);
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
        if(property == null){
            logger.warn("Failed to delete property {}, property not found!", propertyId);
            throw new DataNotFoundException("Property to delete doesn't exist.");
        }
        TransactionServiceImp.getInstance().deletePropertyTransactions(property);
        aerospikeAccess.deleteRecord(property);
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
        logger.info("Updating property {}", id);
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(id);
        if(property == null){
            logger.warn("Failed to delete property {}, property not found!", id);
            throw new DataNotFoundException("Property not found!");
        }
        property.setCost(propertyUpdated.getCost());
        property.setForSale(propertyUpdated.getForSale());
        aerospikeAccess.updateRecord(property);
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
        logger.info("Adding new property");
        OwnerDAO owner = OwnerServiceImp.getInstance().getOwner(ownerUserName);
        int newId = generateUniquePropertyId();
        PropertyDAO newProperty = new PropertyDAO(newId, propertyAddress, owner, cost);
        new AerospikeAccess<>(PropertyDAO.class).saveRecord(newProperty);
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
