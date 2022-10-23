package com.estate.controller.rest;

import com.estate.exception.DataNotFoundException;
import com.estate.model.ErrorMessage;
import com.estate.model.dao.PropertyDAO;
import com.estate.model.dto.PropertyDTO;
import com.estate.model.mapper.PropertyMapper;
import com.estate.service.HypermediaAdder;
import com.estate.service.PropertyService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

/**
 * Property controller is the presentation layer of the application.
 * It exposes the rest APIs related to properties
 */
@Path("properties")
public class PropertyController {
    /**
     * Gets all properties stored in the database.
     *
     * @param uriInfo the uri info
     * @return a list of all properties in dto presentation
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProperties(@Context UriInfo uriInfo) {
        List<PropertyDTO> propertyList;
        try {
            propertyList = PropertyMapper.INSTANCE.propertyListDaoToDto(
                    PropertyService.getInstance().getAllProperties());
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        propertyList.forEach(
                property -> HypermediaAdder.addLink(
                        uriInfo,
                        property,
                        "properties/" + property.getPropertyId(),
                        "self"));
        return Response.ok(propertyList).links(HypermediaAdder.getSelfLink(uriInfo)).build();
    }

    /**
     * Gets a specific property from the database.
     *
     * @param id      the property unique id
     * @param uriInfo the uri info
     * @return the retrieved property in dto presentation
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperty(@PathParam("id") int id, @Context UriInfo uriInfo) {
        PropertyDTO property;
        try {
            property = PropertyMapper.INSTANCE.propertyDaoToDto(
                    PropertyService.getInstance().getProperty(id));
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        HypermediaAdder.addLink(uriInfo, property, "/owners/" + property.getPropertyOwner(), "self");
        return Response.ok(property).links(HypermediaAdder.getSelfLink(uriInfo)).build();
    }

    /**
     * Change property cost and sale state.
     *
     * @param id          the property unique id
     * @param uriInfo     the uri info
     * @param propertyDTO the edited property from the http request body in dto presentation
     * @return the edited property from the database
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePropertyCostAndForSale(@PathParam("id") int id, @Context UriInfo uriInfo, PropertyDTO propertyDTO){
        PropertyDAO propertyUpdatedDAO = PropertyMapper.INSTANCE.propertyDtoToDao(propertyDTO);
        try {
            propertyUpdatedDAO = PropertyService.getInstance().updateProperty(propertyUpdatedDAO, id);
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        PropertyDTO propertyUpdatedDTO = PropertyMapper.INSTANCE.propertyDaoToDto(propertyUpdatedDAO);
        HypermediaAdder.addLink(uriInfo, propertyDTO, "/owners/" + propertyUpdatedDTO.getPropertyOwner(), "info");
        return Response.accepted(propertyUpdatedDTO).links(HypermediaAdder.getSelfLink(uriInfo)).build();
    }

    /**
     * Delete property from the database.
     *
     * @param id the property unique id
     * @return the deleted property in dto presentation
     */
    @DELETE
    @Path("{id}")
    @Produces("application/json")
    public Response deleteProperty(@PathParam("id") int id){
        PropertyDAO property;
        try {
            property = PropertyService.getInstance().deleteProperty(id);
        } catch (DataNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(e.getMessage(), 404))
                    .build();
        }
        PropertyDTO propertyUpdatedDTO = PropertyMapper.INSTANCE.propertyDaoToDto(property);
        return Response.accepted(propertyUpdatedDTO).build();
    }
}
