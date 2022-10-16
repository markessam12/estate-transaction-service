package com.example.service.rest;

import com.example.model.dao.PropertyDAO;
import com.example.model.dto.PropertyDTO;
import com.example.model.mapper.PropertyMapper;
import com.example.resources.AerospikeReader;
import com.example.resources.AerospikeTransactionProcessor;
import com.example.resources.HypermediaCreator;
import com.example.util.AerospikeDB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("properties")
public class PropertyResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProperties(@Context UriInfo uriInfo){
        AerospikeReader<PropertyDAO> recordSet = new AerospikeReader<>(PropertyDAO.class);
        List<PropertyDTO> propertyList = PropertyMapper.INSTANCE.propertyListDaoToDto(
                recordSet.getSet(AerospikeDB.NAMESPACE,  AerospikeDB.PROPERTY, "propertyId")
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
        AerospikeReader<PropertyDAO> reader = new AerospikeReader<>(PropertyDAO.class);
        PropertyDTO property = PropertyMapper.INSTANCE.propertyDaoToDto(
                reader.getRow(id)
        );
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        property.setLinks(
                hypermediaCreator.baseUri("/owners/" + property.getPropertyOwner(), "owner").build()
        );
        return Response.ok(property).links(hypermediaCreator.makeSelfLink()).build();
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePropertyCostAndForSale(@PathParam("id") int id, @Context UriInfo uriInfo, PropertyDTO propertyDTO){
        PropertyDAO property = AerospikeDB.mapper.read(PropertyDAO.class, id);
        property.setForSale(propertyDTO.getForSale());
        property.setCost(propertyDTO.getCost());
        AerospikeDB.mapper.update(property, "forSale","cost");
        PropertyDTO propertyDTOAfterPost = PropertyMapper.INSTANCE.propertyDaoToDto(property);
        HypermediaCreator hypermediaCreator = new HypermediaCreator(uriInfo);
        propertyDTOAfterPost.setLinks(
                hypermediaCreator.baseUri("/owners/" + propertyDTOAfterPost.getPropertyOwner(), "owner").build()
        );
        return Response.ok(propertyDTOAfterPost).links(hypermediaCreator.makeSelfLink()).build();
    }

    @DELETE
    @Path("{id}")
    @Produces("application/json")
    public PropertyDTO deleteProperty(@PathParam("id") int id){
        PropertyDAO property = AerospikeDB.mapper.read(PropertyDAO.class, id);
        new AerospikeTransactionProcessor().deleteTransactionForObject(property);
        AerospikeDB.mapper.delete(property);
        return PropertyMapper.INSTANCE.propertyDaoToDto(property);
    }
}
