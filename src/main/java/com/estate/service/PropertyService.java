package com.estate.service;

import com.estate.Exception.DataNotFoundException;
import com.estate.model.dao.PropertyDAO;
import com.estate.repository.AerospikeAccess;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PropertyService {
    private static PropertyService INSTANCE = new PropertyService();

    private PropertyService(){}

    public static PropertyService getInstance(){
        return INSTANCE;
    }

    public ArrayList<PropertyDAO> getAllProperties(){
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        ArrayList<PropertyDAO> properties = aerospikeAccess.getSet();
//        if(properties.isEmpty())
//            throw new DataNotFoundException("There is no records for any property in the database yet.");
        return properties;
    }

    public PropertyDAO getProperty(int propertyId){
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
//        if(property == null)
//            throw new DataNotFoundException("Property not found!");
        return property;
    }

    public ArrayList<PropertyDAO> getPropertiesOfOwner(String userName){
        ArrayList<PropertyDAO> properties = getAllProperties();
        ArrayList<PropertyDAO> ownerProperties = properties.stream()
                .filter(propertyDAO -> propertyDAO.getPropertyOwner().getUserName().equals(userName))
                .collect(Collectors.toCollection(ArrayList::new));
//        if(ownerProperties.isEmpty())
//            throw new DataNotFoundException("Owner has no properties.");
        return ownerProperties;
    }

    public PropertyDAO deleteProperty(int propertyId){
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(propertyId);
        if(property == null)
            throw new DataNotFoundException("Property not found!");
        TransactionService.getInstance().deletePropertyTransactions(property);
        aerospikeAccess.deleteRecord(propertyId);
        return property;
    }

    public PropertyDAO updateProperty(PropertyDAO propertyUpdated, int id){
        AerospikeAccess<PropertyDAO> aerospikeAccess = new AerospikeAccess<>(PropertyDAO.class);
        PropertyDAO property = aerospikeAccess.getRecord(id);
        property.setCost(propertyUpdated.getCost());
        property.setForSale(propertyUpdated.getForSale());
//        if(aerospikeAccess.getRecord(userName) == null)
//            throw new DataNotFoundException("Owner doesn't exist");
        aerospikeAccess.updateRecord(property);
        return property;
    }

}
