package com.example.service.soap;

import com.example.model.Owner;
import com.example.model.Property;
import com.example.model.Transaction;
import com.example.util.AerospikeDB;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public class SoapServices {
    @WebMethod
    public Owner addUser(String firstName, String lastName, int balance){
        Owner newOwner = new Owner(firstName,lastName,balance);
        AerospikeDB.mapper.save(newOwner);
        return newOwner;
    }

    @WebMethod
    public String addProperty(int ownerID, String propertyAddress, long cost){
        Owner owner = AerospikeDB.mapper.read(Owner.class, ownerID);
        AerospikeDB.mapper.save(new Property(propertyAddress,owner,cost));
        return "Property added successfully!";
    }

    @WebMethod
    public String addBalance(int id, int balance){
        Owner owner = AerospikeDB.mapper.read(Owner.class, id);
        owner.setBalance(owner.getBalance() + balance);
        AerospikeDB.mapper.save(owner);
        return "Balance added successfully!";
    }

    @WebMethod
    public synchronized String buyProperty(int buyerID, int propertyID){
        Owner buyer, seller;
        Property property;
        try{
            buyer = AerospikeDB.mapper.read(Owner.class, buyerID);
            property = AerospikeDB.mapper.read(Property.class, propertyID);
            seller = AerospikeDB.mapper.read(Owner.class, property.getPropertyOwner().getId());
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
        AerospikeDB.mapper.save(new Transaction(seller, buyer, property));
        AerospikeDB.mapper.update(buyer,"balance");
        AerospikeDB.mapper.update(seller, "balance");
        AerospikeDB.mapper.update(property, "propertyOwner","forSale");
        return "Property bought successfully!";
    }
}
