package com.estate.service;

import com.estate.exception.DataNotFoundException;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.repository.AerospikeAccess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

public class PropertyService {
    private static final PropertyService INSTANCE = new PropertyService();

    private PropertyService(){}

    public static PropertyService getInstance(){
        return INSTANCE;
    }

    public ArrayList<PropertyDAO> getAllProperties() throws DataNotFoundException{
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        ArrayList<PropertyDAO> properties = aerospikeAccess.getSet();
        if(properties.isEmpty())
            throw new DataNotFoundException("There is no records for any property in the database.");
        return properties;
    }

    public PropertyDAO getProperty(int propertyId) throws DataNotFoundException{
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
        if(property == null)
            throw new DataNotFoundException("Property not found!");
        return property;
    }

    public ArrayList<PropertyDAO> getPropertiesOfOwner(String userName) throws DataNotFoundException {
        ArrayList<PropertyDAO> properties = getAllProperties();
        ArrayList<PropertyDAO> ownerProperties = properties.stream()
                .filter(propertyDAO -> propertyDAO.getPropertyOwner().getUserName().equals(userName))
                .collect(Collectors.toCollection(ArrayList::new));
        if(ownerProperties.isEmpty())
            throw new DataNotFoundException("Owner has no properties.");
        return ownerProperties;
    }

    public PropertyDAO deleteProperty(int propertyId) throws DataNotFoundException{
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
        if(property == null)
            throw new DataNotFoundException("Property to delete doesn't exist.");
        TransactionService.getInstance().deletePropertyTransactions(property);
        aerospikeAccess.deleteRecord(property);
        return property;
    }

    public PropertyDAO updateProperty(PropertyDAO propertyUpdated, int id) throws DataNotFoundException {
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(id);
        if(property == null){
            throw new DataNotFoundException("Property not found!");
        }
        property.setCost(propertyUpdated.getCost());
        property.setForSale(propertyUpdated.getForSale());
        aerospikeAccess.updateRecord(property);
        return property;
    }

    public PropertyDAO addProperty(String ownerUserName, String propertyAddress, long cost) throws DataNotFoundException{
        AerospikeAccess<PropertyDAO> aerospikePropertyAccess = new AerospikeAccess<>(PropertyDAO.class);
        OwnerDAO owner = OwnerService.getInstance().getOwner(ownerUserName);
        Optional<PropertyDAO> propertyWithHighestID =  aerospikePropertyAccess.getSet()
                .stream()
                .max(Comparator.comparing(PropertyDAO::getPropertyId));
        int highestID = 0;
        if(propertyWithHighestID.isPresent())
            highestID = propertyWithHighestID.get().getPropertyId();
        PropertyDAO newProperty = new PropertyDAO(highestID + 1, propertyAddress, owner, cost);
        aerospikePropertyAccess.saveRecord(newProperty);
        return newProperty;
    }

}
