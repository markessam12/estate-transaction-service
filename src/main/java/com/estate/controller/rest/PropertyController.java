package com.estate.controller.rest;

import com.estate.model.dao.PropertyDAO;
import com.estate.model.dto.PropertyDTO;
import com.estate.model.mapper.PropertyMapper;
import com.estate.repository.HypermediaCreator;
import com.estate.service.PropertyService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("properties")
public class PropertyController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProperties(@Context UriInfo uriInfo){
        List<PropertyDTO> propertyList = PropertyMapper.INSTANCE.propertyListDaoToDto(
                PropertyService.getInstance().getAllProperties()
        );
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        propertyList.forEach(
                property -> property.setLinks(
                        hypermediaCreator.requestUri(Integer.toString(property.getPropertyId()), "self").build()
                )
        );
        return Response.ok(propertyList).links(hypermediaCreator.makeSelfLink()).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperty(@PathParam("id") int id, @Context UriInfo uriInfo){
        PropertyDTO property = PropertyMapper.INSTANCE.propertyDaoToDto(
                PropertyService.getInstance().getProperty(id)
        );
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        property.setLinks(
                hypermediaCreator.baseUri("/owners/" + property.getPropertyOwner(), "owner").build()
        );
        return Response.ok(property).links(hypermediaCreator.makeSelfLink()).build();
    }

    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePropertyCostAndForSale(@PathParam("id") int id, @Context UriInfo uriInfo, PropertyDTO propertyDTO){
        PropertyDAO propertyUpdatedDAO = PropertyMapper.INSTANCE.propertyDtoToDao(propertyDTO);
        propertyUpdatedDAO = PropertyService.getInstance().updateProperty(propertyUpdatedDAO, id);
        PropertyDTO propertyUpdatedDTO = PropertyMapper.INSTANCE.propertyDaoToDto(propertyUpdatedDAO);
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        propertyUpdatedDTO.setLinks(
                hypermediaCreator.baseUri("/owners/" + propertyUpdatedDTO.getPropertyOwner(), "owner").build()
        );
        return Response.ok(propertyUpdatedDTO).links(hypermediaCreator.makeSelfLink()).build();
    }

    @DELETE
    @Path("{id}")
    @Produces("application/json")
    public PropertyDTO deleteProperty(@PathParam("id") int id){
        PropertyDAO property = PropertyService.getInstance().deleteProperty(id);
        return PropertyMapper.INSTANCE.propertyDaoToDto(property);
    }
}
