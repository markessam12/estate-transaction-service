package com.estate.controller.rest;

import com.estate.exception.DataNotFoundException;
import com.estate.exception.RequestFailedException;
import com.estate.model.ErrorMessage;
import com.estate.model.dao.OwnerDAO;
import com.estate.model.dao.TransactionDAO;
import com.estate.model.dto.OwnerDTO;
import com.estate.model.dto.PropertyDTO;
import com.estate.model.dto.TransactionDTO;
import com.estate.model.mapper.OwnerMapper;
import com.estate.model.mapper.PropertyMapper;
import com.estate.model.mapper.TransactionMapper;
import com.estate.service.HypermediaAdder;
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
        ArrayList<OwnerDTO> owners;
        try {
            owners = OwnerMapper.INSTANCE.ownerListDaoToDto(
                    OwnerService.getInstance().getAllOwners());
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        owners.forEach(owner -> HypermediaAdder.addLink(uriInfo, owner, "owners/" + owner.getUserName(), "self"));
        return Response.ok(owners).links(HypermediaAdder.getSelfLink(uriInfo)).build();
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwner(@PathParam("username") String userName, @Context UriInfo uriInfo){
        OwnerDTO ownerDTO;
        try {
            ownerDTO = OwnerMapper.INSTANCE.ownerDaoToDto(
                    OwnerService.getInstance().getOwner(userName));
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        ArrayList<PropertyDTO> ownerProperties;
        try {
            ownerProperties = PropertyMapper.INSTANCE.propertyListDaoToDto(
                    PropertyService.getInstance().getPropertiesOfOwner(userName));
            ownerProperties.forEach(propertyDTO ->
                            HypermediaAdder.addLink(
                                    uriInfo,
                                    propertyDTO,
                                    "/properties/" + propertyDTO.getPropertyId(),
                                    "self"));
        } catch (DataNotFoundException ignored) {
            ownerProperties = null;
        }
        ownerDTO.setOwnedProperties(ownerProperties);
            return Response.ok(ownerDTO).links(HypermediaAdder.getSelfLink(uriInfo)).build();
    }

    @DELETE
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOwner(@PathParam("username") String userName) {
        OwnerDAO owner;
        try {
            owner = OwnerService.getInstance().deleteOwner(userName);
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        } catch (RequestFailedException e) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                    .entity(new ErrorMessage(e.getMessage(), 405))
                    .build();
        }
        return Response.accepted(OwnerMapper.INSTANCE.ownerDaoToDto(owner)).build();
    }

    @PUT
    @Path("{username}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateOwner(@PathParam("username") String userName, OwnerDTO ownerDTO, @Context UriInfo uriInfo){
        ownerDTO.setUserName(userName);
        OwnerDAO ownerUpdatedDAO = OwnerMapper.INSTANCE.ownerDtoToDao(ownerDTO);
        try {
            OwnerService.getInstance().updateOwner(ownerUpdatedDAO, userName);
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        OwnerDTO ownerUpdatedDTO = OwnerMapper.INSTANCE.ownerDaoToDto(ownerUpdatedDAO);
        HypermediaAdder.addLink(uriInfo, ownerUpdatedDTO, "owners/" + ownerUpdatedDTO.getUserName(), "self");
        return Response.accepted(ownerUpdatedDTO).links(HypermediaAdder.getSelfLink(uriInfo)).build();
    }

    @GET
    @Path("{username}/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOwnerProperties(@PathParam("username") String userName, @Context UriInfo uriInfo){
        OwnerDAO owner;
        ArrayList<TransactionDAO> ownerTransactionsDAO;
        try {
            owner = OwnerService.getInstance().getOwner(userName);
            ownerTransactionsDAO = TransactionService.getInstance().getOwnerTransactions(owner);
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        ArrayList<TransactionDTO> ownerTransactionsDTO = TransactionMapper.INSTANCE.transactionListDaoToDto(ownerTransactionsDAO);
        TransactionService.getInstance().addHypermediaToTransactions(ownerTransactionsDTO, uriInfo);
        return Response.ok(ownerTransactionsDTO).links(HypermediaAdder.getSelfLink(uriInfo)).build();
    }
}
