package com.estate.service;

import com.estate.model.dto.HATEOAS;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;
import org.jetbrains.annotations.NotNull;

/**
 * The hypermedia adder is used to fill the links attribute of classes extending the HATEOAS class.
 */
//This class helps in applying HATEOAS constraint by created the hypermedia links
public class HypermediaAdder {
    private HypermediaAdder(){}

    /**
     * Add hypermedia link to the object.
     *
     * @param uriInfo  the uri info
     * @param entity   the object extending HATEOAS to which the link is added
     * @param path     the path to the referenced resource
     * @param relation the relation to the referenced resource
     */
    public static void addLink(@NotNull UriInfo uriInfo, @NotNull HATEOAS entity, String path, String relation) {
        entity.addLink(
                Link.fromUriBuilder(
                            uriInfo.getBaseUriBuilder().path(path)
                        )
                        .rel(relation)
                        .type("GET")
                        .build()
        );
    }

    /**
     * Get the resource link of the object itself to directly access it.
     *
     * @param uriInfo the uri info
     * @return the object link
     */
    public static Link getSelfLink(@NotNull UriInfo uriInfo){
        return Link.fromUriBuilder(uriInfo.getAbsolutePathBuilder())
                .rel("self").build();
    }
}
