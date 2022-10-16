package com.example.service.soap;

import com.example.model.dao.OwnerDAO;
import com.example.model.dao.PropertyDAO;
import com.example.model.dao.TransactionDAO;
import com.example.util.AerospikeDB;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public class SoapServices {
    @WebMethod
    public OwnerDAO addUser(String userName, String firstName, String lastName, Long balance){
        OwnerDAO newOwner = new OwnerDAO(userName, firstName, lastName, balance);
        AerospikeDB.mapper.save(newOwner);
        return newOwner;
    }

    @WebMethod
    public String addProperty(String userName, String propertyAddress, long cost){
        OwnerDAO owner = AerospikeDB.mapper.read(OwnerDAO.class, userName);
        if(owner == null)
            return "Owner not found!";
        AerospikeDB.mapper.save(new PropertyDAO(propertyAddress,owner,cost));
        return "Property added successfully!";
    }

    @WebMethod
    public String addBalance(String userName, int balance){
        OwnerDAO owner = AerospikeDB.mapper.read(OwnerDAO.class, userName);
        if(owner == null)
            return "Owner not found!";
        owner.setBalance(owner.getBalance() + balance);
        AerospikeDB.mapper.save(owner);
        return "Balance added successfully!";
    }

    @WebMethod
    public synchronized String buyProperty(String buyerID, int propertyID){
        OwnerDAO buyer, seller;
        PropertyDAO property;
        try{
            buyer = AerospikeDB.mapper.read(OwnerDAO.class, buyerID);
            property = AerospikeDB.mapper.read(PropertyDAO.class, propertyID);
            seller = AerospikeDB.mapper.read(OwnerDAO.class, property.getPropertyOwner().getUserName());
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
        AerospikeDB.mapper.save(new TransactionDAO(seller, buyer, property));
        AerospikeDB.mapper.update(buyer,"balance");
        AerospikeDB.mapper.update(seller, "balance");
        AerospikeDB.mapper.update(property, "propertyOwner","forSale");
        return "Property bought successfully!";
    }
}
