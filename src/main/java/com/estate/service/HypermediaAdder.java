package com.estate.service;

import com.estate.model.dto.HATEOAS;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;
import org.jetbrains.annotations.NotNull;

//This class helps in applying HATEOAS constraint by created the hypermedia links
public class HypermediaAdder {
    private HypermediaAdder(){}

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

    public static Link getSelfLink(@NotNull UriInfo uriInfo){
        return Link.fromUriBuilder(uriInfo.getAbsolutePathBuilder())
                .rel("self").build();
    }
}
