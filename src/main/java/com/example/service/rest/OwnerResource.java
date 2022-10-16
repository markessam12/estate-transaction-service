package com.example.service.rest;

import com.example.model.dao.OwnerDAO;
import com.example.model.dao.PropertyDAO;
import com.example.model.dto.OwnerDTO;
import com.example.model.dto.PropertyDTO;
import com.example.model.mapper.OwnerMapper;
import com.example.model.mapper.PropertyMapper;
import com.example.resources.AerospikeReader;
import com.example.resources.AerospikeTransactionProcessor;
import com.example.resources.HypermediaCreator;
import com.example.util.AerospikeDB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("owners")
public class OwnerResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOwner(@Context UriInfo uriInfo){
        AerospikeReader<OwnerDAO> recordSet = new AerospikeReader<>(OwnerDAO.class);
        ArrayList<OwnerDTO> owners = OwnerMapper.INSTANCE.ownerListDaoToDto(
                recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.OWNERSHIP, "userName")
        );
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        owners.forEach(owner -> owner.setLinks(
                hypermediaCreator.requestUri(owner.getUserName(), "self").build()
        ));
        return Response.ok(owners).links(hypermediaCreator.makeSelfLink()).build();
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwner(@PathParam("username") String userName, @Context UriInfo uriInfo){
        AerospikeReader<OwnerDAO> reader = new AerospikeReader<>(OwnerDAO.class);
        OwnerDAO owner = reader.getRow(userName);
        OwnerDTO ownerDTO = OwnerMapper.INSTANCE.ownerDaoToDto(owner);
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        List<PropertyDTO> ownerProperties = getAllOwnerPropertiesAsList(ownerDTO.getUserName());
        ownerProperties.forEach(propertyDTO -> propertyDTO.setLinks(
                hypermediaCreator.baseUri("/properties/" + propertyDTO.getPropertyId(), "self").build()
        ));
        ownerDTO.setOwnedProperties(ownerProperties);
        return Response.ok(ownerDTO).links(hypermediaCreator.makeSelfLink()).build();
    }

    @DELETE
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOwner(@PathParam("username") String userName){
        List<PropertyDTO> properties = getAllOwnerPropertiesAsList(userName);
        if(!properties.isEmpty())
            throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
        OwnerDAO owner = AerospikeDB.mapper.read(OwnerDAO.class, userName);
        new AerospikeTransactionProcessor().deleteTransactionForObject(owner);
        AerospikeDB.mapper.delete(owner);
        return Response.ok(OwnerMapper.INSTANCE.ownerDaoToDto(owner)).build();
    }

    @POST
    @Path("{username}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response changeOwnerName(@PathParam("username") String userName, OwnerDTO ownerDTO, @Context UriInfo uriInfo){
        OwnerDAO owner = AerospikeDB.mapper.read(OwnerDAO.class, userName);
        owner.setFirstName(ownerDTO.getFirstName());
        owner.setLastName(ownerDTO.getLastName());
        AerospikeDB.mapper.update(owner,"firstName","lastName");
        OwnerDTO ownerDTOSent = OwnerMapper.INSTANCE.ownerDaoToDto(owner);
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        ownerDTOSent.setLinks(
                hypermediaCreator.requestUri("","self").build()
        );
        return Response.ok(ownerDTOSent).links(hypermediaCreator.makeSelfLink()).build();
    }

    @GET
    @Path("{username}/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOwnerProperties(@PathParam("username") String userName, @Context UriInfo uriInfo){
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        List<PropertyDTO> propertyList = getAllOwnerPropertiesAsList(userName);
        propertyList.forEach(propertyDTO -> propertyDTO.setLinks(
                hypermediaCreator.baseUri("/properties/" + propertyDTO.getPropertyId(),"self").build()
        ));
        return Response.ok(propertyList).links(hypermediaCreator.makeSelfLink()).build();
    }

    private List<PropertyDTO> getAllOwnerPropertiesAsList(String userName){
        AerospikeReader<PropertyDAO> recordSet = new AerospikeReader<>(PropertyDAO.class);
        ArrayList<PropertyDAO> ownerProperties = recordSet
                .getSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId")
                .stream()
                .filter(propertyDAO -> propertyDAO.getPropertyOwner().getUserName().equals(userName))
                .collect(Collectors.toCollection(ArrayList::new));
        return PropertyMapper.INSTANCE.propertyListDaoToDto(ownerProperties);
    }
}
