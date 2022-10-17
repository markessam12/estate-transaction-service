package com.estate.controller.rest;

import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.OwnerDTO;
import com.estate.model.dto.PropertyDTO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.OwnerMapper;
import com.estate.model.mapper.PropertyMapper;
import com.estate.model.mapper.TransactionMapper;
import com.estate.repository.HypermediaCreator;
import com.estate.service.OwnerService;
import com.estate.service.PropertyService;
import com.estate.service.TransactionService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.ArrayList;

@Path("owners")
public class OwnerController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOwners(@Context UriInfo uriInfo){
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        ArrayList<OwnerDTO> owners = OwnerMapper.INSTANCE.ownerListDaoToDto(
                OwnerService.getInstance().getAllOwners()
        );
        owners.forEach(owner -> owner.setLinks(
                hypermediaCreator.requestUri(owner.getUserName(), "self").build()
        ));
        return Response.ok(owners).links(hypermediaCreator.makeSelfLink()).build();
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwner(@PathParam("username") String userName, @Context UriInfo uriInfo){
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        OwnerDTO ownerDTO = OwnerMapper.INSTANCE.ownerDaoToDto(
                OwnerService.getInstance().getOwner(userName)
        );
        ArrayList<PropertyDTO> ownerProperties = PropertyMapper.INSTANCE.propertyListDaoToDto(
                PropertyService.getInstance().getPropertiesOfOwner(userName)
        );
        ownerDTO.setOwnedProperties(ownerProperties);
        ownerProperties.forEach(propertyDTO -> propertyDTO.setLinks(
                hypermediaCreator.baseUri("/properties/" + propertyDTO.getPropertyId(), "self").build()
        ));
        return Response.ok(ownerDTO).links(hypermediaCreator.makeSelfLink()).build();
    }

    @DELETE
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOwner(@PathParam("username") String userName){
        OwnerDAO owner = OwnerService.getInstance().deleteOwner(userName);
        return Response.ok(OwnerMapper.INSTANCE.ownerDaoToDto(owner)).build();
    }

    @PATCH
    @Path("{username}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateOwner(@PathParam("username") String userName, OwnerDTO ownerDTO, @Context UriInfo uriInfo){
        ownerDTO.setUserName(userName);
        OwnerDAO ownerUpdatedDAO = OwnerMapper.INSTANCE.ownerDtoToDao(ownerDTO);
        OwnerService.getInstance().updateOwner(ownerUpdatedDAO, userName);
        OwnerDTO ownerUpdatedDTO = OwnerMapper.INSTANCE.ownerDaoToDto(ownerUpdatedDAO);
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        ownerUpdatedDTO.setLinks(
                hypermediaCreator.requestUri("","self").build()
        );
        return Response.ok(ownerUpdatedDTO).links(hypermediaCreator.makeSelfLink()).build();
    }

    @GET
    @Path("{username}/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOwnerProperties(@PathParam("username") String userName, @Context UriInfo uriInfo){
        OwnerDAO owner = OwnerService.getInstance().getOwner(userName);
        ArrayList<TransactionDAO> ownerTransactionsDAO = TransactionService.getInstance().getOwnerTransactions(owner);
        ArrayList<TransactionDTO> ownerTransactionsDTO = TransactionMapper.INSTANCE.transactionListDaoToDto(ownerTransactionsDAO);
        ownerTransactionsDTO = TransactionService.getInstance().addHypermediaToTransactions(ownerTransactionsDTO, uriInfo);
        return Response.ok(ownerTransactionsDTO).links(new HypermediaCreator(uriInfo).makeSelfLink()).build();
    }
}
