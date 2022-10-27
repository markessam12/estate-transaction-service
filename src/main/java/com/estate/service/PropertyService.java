package com.estate.service;

import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.repository.AerospikeAccess;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The singleton class representing the property service layer and
 * providing all property services to the Controller layer
 */
public interface PropertyService {
    /**
     * Gets all properties of a specific owner.
     *
     * @param userName the username
     * @return the list of properties of owner
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<PropertyDAO> getPropertiesOfOwner(String userName) throws DataNotFoundException;

    /**
     * Gets all properties in the database.
     *
     * @return the list of properties
     * @throws DataNotFoundException the data not found exception
     */
    public ArrayList<PropertyDAO> getAllProperties() throws DataNotFoundException;

    /**
     * Gets a specific property from the database.
     *
     * @param propertyId he unique property id
     * @return the retrieved property
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO getProperty(int propertyId) throws DataNotFoundException;

    /**
     * Delete a specific property from the database.
     *
     * @param propertyId the unique property id
     * @return the deleted property
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO deleteProperty(int propertyId) throws DataNotFoundException;

    /**
     * Update a specific property in the database.
     *
     * @param propertyUpdated the updated property data sent from controller layer
     * @param id              the unique property id
     * @return the updated property data retrieved from database
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO updateProperty(PropertyDAO propertyUpdated, int id) throws DataNotFoundException;

    /**
     * Add a new property to the database.
     *
     * @param ownerUserName   the owner username
     * @param propertyAddress the property address
     * @param cost            the cost
     * @return the new property data
     * @throws DataNotFoundException the data not found exception
     */
    public PropertyDAO addProperty(String ownerUserName, String propertyAddress, long cost) throws DataNotFoundException;

    /**
     * Generate new unique property id.
     *
     * @return the new id of type int
     */
    public int generateUniquePropertyId();
}
