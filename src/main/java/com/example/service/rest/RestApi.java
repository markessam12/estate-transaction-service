package com.example.service.rest;

import com.example.model.Owner;
import com.example.model.Property;
import com.example.resources.RowReader;
import com.example.resources.TableReader;
import com.example.util.AerospikeDB;
import jakarta.ws.rs.*;

import java.util.ArrayList;
import java.util.List;

@Path("/estate")
public class RestApi {
    @GET
    @Path("/owners/{id}")
    @Produces("application/json")
    public Owner getOwner(@PathParam("id") int id){
        RowReader<Owner> reader = new RowReader<Owner>(Owner.class);
        return reader.getRaw(id);
    }

    @GET
    @Path("/owners")
    @Produces("application/json")
    public List<Owner> getAllOwner(){
        TableReader<Owner> recordSet = new TableReader<>(Owner.class);
        return recordSet.getAllSet(AerospikeDB.NAMESPACE,  AerospikeDB.OWNERSHIP, "accountId");
    }

    @DELETE
    @Path("/owners/{id}")
    @Produces("application/json")
    public Owner deleteOwner(@PathParam("id") int id){
        Owner owner = AerospikeDB.mapper.read(Owner.class, id);
        AerospikeDB.mapper.delete(owner);
        return owner;
    }

    @GET
    @Path("/properties/{id}")
    @Produces("application/json")
    public Property getProperty(@PathParam("id") int id){
        RowReader<Property> reader = new RowReader<Property>(Property.class);
        return reader.getRaw(id);
    }

    @GET
    @Path("/properties")
    @Produces("application/json")
    public List<Property> getAllProperties(){
        TableReader<Property> recordSet = new TableReader<>(Property.class);
        return recordSet.getAllSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId");
    }

    @DELETE
    @Path("/properties/{id}")
    @Produces("application/json")
    public Property deleteProperty(@PathParam("id") int id){
        Property property = AerospikeDB.mapper.read(Property.class, id);
        AerospikeDB.mapper.delete(property);
        return property;
    }

    @PATCH
    @Path("/properties/{id}")
    @Produces("application/json")
    public Property changeSaleProperty(@PathParam("id") int id, @QueryParam("forsale") int forSale){
        Property property = AerospikeDB.mapper.read(Property.class, id);
        property.setForSale(forSale);
        AerospikeDB.mapper.update(property, "forSale");
        return property;
    }

    @GET
    @Path("/owners/{ownerID}/properties")
    @Produces("application/json")
    public List<Property> getAllProperties(@PathParam("ownerID") int ownerID){
        TableReader<Property> recordSet = new TableReader<>(Property.class);
        List<Property> propertyList = recordSet.getAllSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId");
        List<Property> ownerProperties = new ArrayList<>();
        for (Property propery: propertyList) {
            if(propery.getPropertyOwner().getId() == ownerID){
                propery.setPropertyOwner(null);
                ownerProperties.add(propery);
            }
        }
        return ownerProperties;
    }
}
