package com.estate.controller.soap;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.repository.AerospikeAccess;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

import java.util.Comparator;
import java.util.Optional;

@WebService
public class SoapServices {
    @WebMethod
    public OwnerDAO addUser(String userName, String firstName, String lastName, Long balance){
        OwnerDAO newOwner = new OwnerDAO(userName, firstName, lastName, balance);
        AerospikeAccess<OwnerDAO> aerospikeAccess = new AerospikeAccess<>(OwnerDAO.class);
        aerospikeAccess.saveRecord(newOwner);
        return newOwner;
    }

    @WebMethod
    public String addProperty(String userName, String propertyAddress, long cost){
        OwnerDAO owner = AerospikeAccess.mapper.read(OwnerDAO.class, userName);
        if(owner == null)
            return "Owner not found!";
        AerospikeAccess<PropertyDAO> reader = new AerospikeAccess<>(PropertyDAO.class);
        Optional<PropertyDAO> propertyWithHighestID =  reader.getSet()
                    .stream()
                    .max(Comparator.comparing(PropertyDAO::getPropertyId));
        int highestID = 0;
        if(propertyWithHighestID.isPresent())
            highestID = propertyWithHighestID.get().getPropertyId();
        AerospikeAccess.mapper.save(new PropertyDAO(highestID + 1, propertyAddress, owner, cost));
        return "Property added successfully!";
    }

    @WebMethod
    public String addBalance(String userName, int balance){
        OwnerDAO owner = AerospikeAccess.mapper.read(OwnerDAO.class, userName);
        if(owner == null)
            return "Owner not found!";
        owner.setBalance(owner.getBalance() + balance);
        AerospikeAccess.mapper.save(owner);
        return "Balance added successfully!";
    }

    @WebMethod
    public synchronized String buyProperty(String buyerID, int propertyID){
        OwnerDAO buyer, seller;
        PropertyDAO property;
        try{
            buyer = AerospikeAccess.mapper.read(OwnerDAO.class, buyerID);
            property = AerospikeAccess.mapper.read(PropertyDAO.class, propertyID);
            seller = AerospikeAccess.mapper.read(OwnerDAO.class, property.getPropertyOwner().getUserName());
        }catch (Exception e){
            return "Incorrect Id";
        }
        if(property.getForSale() == 0){
            return "Purchase fail! \nProperty not for sale.";
        }
        if(buyer.getBalance() < property.getCost()){
            return "Insufficient balance";
        }
        seller.setBalance(seller.getBalance() + property.getCost());
        buyer.setBalance(buyer.getBalance() - property.getCost());
        property.setPropertyOwner(buyer);
        property.setForSale(0);
        AerospikeAccess.mapper.save(new TransactionDAO(seller, buyer, property));
        AerospikeAccess.mapper.update(buyer,"balance");
        AerospikeAccess.mapper.update(seller, "balance");
        AerospikeAccess.mapper.update(property, "propertyOwner","forSale");
        return "Property bought successfully!";
    }
}
