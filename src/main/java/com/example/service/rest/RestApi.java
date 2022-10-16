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
import com.example.resources.AerospikeTransactionProcessor;
import com.example.util.AerospikeDB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/estate")
public class RestApi {
    @GET
    @Path("/owners/{id}")
    @Produces("application/json")
    public OwnerDTO getOwner(@PathParam("id") String id){
        AerospikeReader<OwnerDAO> reader = new AerospikeReader<>(OwnerDAO.class);
        OwnerDAO owner = reader.getRow(id);
        OwnerDTO ownerDTO = OwnerMapper.INSTANCE.ownerDaoToDto(owner);
        ownerDTO.setOwnedProperties(getAllOwnerProperties(ownerDTO.getUserName()));
        return ownerDTO;
    }

    @GET
    @Path("/owners")
    @Produces("application/json")
    public List<OwnerDTO> getAllOwner(){
        AerospikeReader<OwnerDAO> recordSet = new AerospikeReader<>(OwnerDAO.class);
        return OwnerMapper.INSTANCE.ownerListDaoToDto(
                recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.OWNERSHIP, "userName")
        );
    }

    @DELETE
    @Path("/owners/{id}")
    @Produces("application/json")
    public OwnerDTO deleteOwner(@PathParam("id") String id){
        List<PropertyDTO> properties = getAllOwnerProperties(id);
        if(!properties.isEmpty())
            throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
        OwnerDAO owner = AerospikeDB.mapper.read(OwnerDAO.class, id);
        new AerospikeTransactionProcessor().deleteTransactionForObject(owner);
        AerospikeDB.mapper.delete(owner);
        return OwnerMapper.INSTANCE.ownerDaoToDto(owner);
    }

    @GET
    @Path("/properties/{id}")
    @Produces("application/json")
    public PropertyDTO getProperty(@PathParam("id") int id){
        AerospikeReader<PropertyDAO> reader = new AerospikeReader<>(PropertyDAO.class);
        return PropertyMapper.INSTANCE.propertyDaoToDto(
                reader.getRow(id)
        );
    }

    @GET
    @Path("/properties")
    @Produces("application/json")
    public List<PropertyDTO> getAllProperties(){
        AerospikeReader<PropertyDAO> recordSet = new AerospikeReader<>(PropertyDAO.class);
        return PropertyMapper.INSTANCE.propertyListDaoToDto(
                recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId")
        );
    }

    @DELETE
    @Path("/properties/{id}")
    @Produces("application/json")
    public PropertyDTO deleteProperty(@PathParam("id") int id){
        PropertyDAO property = AerospikeDB.mapper.read(PropertyDAO.class, id);
        new AerospikeTransactionProcessor().deleteTransactionForObject(property);
        AerospikeDB.mapper.delete(property);
        return PropertyMapper.INSTANCE.propertyDaoToDto(property);
    }

    @POST
    @Path("/properties/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public PropertyDTO changePropertyCostAndForSale(@PathParam("id") int id, PropertyDTO propertyDTO){
        PropertyDAO property = AerospikeDB.mapper.read(PropertyDAO.class, id);
        property.setForSale(propertyDTO.getForSale());
        property.setCost(propertyDTO.getCost());
        AerospikeDB.mapper.update(property, "forSale","cost");
        return PropertyMapper.INSTANCE.propertyDaoToDto(property);
    }

    @POST
    @Path("/owners/{username}")
    @Consumes("application/json")
    @Produces("application/json")
    public OwnerDTO changeOwnerName(@PathParam("username") String userName, OwnerDTO ownerDTO){
        OwnerDAO owner = AerospikeDB.mapper.read(OwnerDAO.class, userName);
        owner.setFirstName(ownerDTO.getFirstName());
        owner.setLastName(ownerDTO.getLastName());
        AerospikeDB.mapper.update(owner,"firstName","lastName");
        return OwnerMapper.INSTANCE.ownerDaoToDto(owner);
    }

    @GET
    @Path("/owners/{ownerID}/properties")
    @Produces("application/json")
    public List<PropertyDTO> getAllOwnerProperties(@PathParam("ownerID") String userName){
        AerospikeReader<PropertyDAO> recordSet = new AerospikeReader<>(PropertyDAO.class);
        ArrayList<PropertyDAO> ownerProperties = recordSet
                .getSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId")
                .stream()
                .filter(propertyDAO -> propertyDAO.getPropertyOwner().getUserName().equals(userName))
                .collect(Collectors.toCollection(ArrayList::new));
        return PropertyMapper.INSTANCE.propertyListDaoToDto(ownerProperties);
    }

    @GET
    @Path("/transactions")
    @Produces("application/json")
    public List<TransactionDTO> getAllTransactions(){
        AerospikeReader<TransactionDAO> recordSet = new AerospikeReader<>(TransactionDAO.class);
        ArrayList<TransactionDAO> set = recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.TRANSACTION, "date");
        return TransactionMapper.INSTANCE.transactionListDaoToDto(set);
    }
}
