package com.example.service.rest;

import com.example.model.dao.OwnerDAO;
import com.example.model.dao.PropertyDAO;
import com.example.model.dao.TransactionDAO;
import com.example.model.dto.OwnerDTO;
import com.example.model.dto.PropertyDTO;
import com.example.model.dto.TransactionDTO;
import com.example.model.mapper.OwnerMapper;
import com.example.model.mapper.PropertyMapper;
import com.example.model.mapper.TransactionMapper;
import com.example.resources.AerospikeReader;
import com.example.util.AerospikeDB;
import jakarta.ws.rs.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/estate")
public class RestApi {
    @GET
    @Path("/owners/{id}")
    @Produces("application/xml")
    public OwnerDTO getOwner(@PathParam("id") String id){
        AerospikeReader<OwnerDAO> reader = new AerospikeReader<OwnerDAO>(OwnerDAO.class);
        OwnerDAO owner = reader.getRow(id);
        return OwnerMapper.INSTANCE.ownerDaoToDto(owner);
    }

    @GET
    @Path("/owners")
    @Produces("application/xml")
    public List<OwnerDTO> getAllOwner(){
        AerospikeReader<OwnerDAO> recordSet = new AerospikeReader<>(OwnerDAO.class);
        return OwnerMapper.INSTANCE.ownerListDaoToDto(
                recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.OWNERSHIP, "userName")
        );
    }

    @DELETE
    @Path("/owners/{id}")
    @Produces("application/xml")
    public OwnerDTO deleteOwner(@PathParam("id") String id){
        OwnerDAO owner = AerospikeDB.mapper.read(OwnerDAO.class, id);
        AerospikeDB.mapper.delete(owner);
        return OwnerMapper.INSTANCE.ownerDaoToDto(owner);
    }

    @GET
    @Path("/properties/{id}")
    @Produces("application/xml")
    public PropertyDTO getProperty(@PathParam("id") int id){
        AerospikeReader<PropertyDAO> reader = new AerospikeReader<PropertyDAO>(PropertyDAO.class);
        return PropertyMapper.INSTANCE.propertyDaoToDto(
                reader.getRow(id)
        );
    }

    @GET
    @Path("/properties")
    @Produces("application/xml")
    public List<PropertyDTO> getAllProperties(){
        AerospikeReader<PropertyDAO> recordSet = new AerospikeReader<>(PropertyDAO.class);
        return PropertyMapper.INSTANCE.propertyListDaoToDto(
                recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId")
        );
    }

    @DELETE
    @Path("/properties/{id}")
    @Produces("application/xml")
    public PropertyDTO deleteProperty(@PathParam("id") int id){
        PropertyDAO property = AerospikeDB.mapper.read(PropertyDAO.class, id);
        AerospikeDB.mapper.delete(property);
        return PropertyMapper.INSTANCE.propertyDaoToDto(property);
    }

    @PATCH
    @Path("/properties/{id}")
    @Produces("application/json")
    public PropertyDTO changeSaleProperty(@PathParam("id") int id, @QueryParam("forsale") int forSale){
        PropertyDAO property = AerospikeDB.mapper.read(PropertyDAO.class, id);
        property.setForSale(forSale);
        AerospikeDB.mapper.update(property, "forSale");
        return PropertyMapper.INSTANCE.propertyDaoToDto(property);
    }

    @GET
    @Path("/owners/{ownerID}/properties")
    @Produces("application/xml")
    public List<PropertyDTO> getAllProperties(@PathParam("ownerID") String userName){
        AerospikeReader<PropertyDAO> recordSet = new AerospikeReader<>(PropertyDAO.class);
        ArrayList<PropertyDAO> ownerProperties = recordSet
                .getSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId")
                .stream()
                .filter(propertyDAO -> {
                    return propertyDAO.getPropertyOwner().getUserName().equals(userName);
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return PropertyMapper.INSTANCE.propertyListDaoToDto(ownerProperties);
    }

    @GET
    @Path("/transactions")
    @Produces("application/xml")
    public List<TransactionDTO> getAllTransactions(){
        AerospikeReader<TransactionDAO> recordSet = new AerospikeReader<>(TransactionDAO.class);
        ArrayList<TransactionDAO> set = recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.TRANSACTION, "transactionId");
        return TransactionMapper.INSTANCE.transactionListDaoToDto(set);
    }
}
