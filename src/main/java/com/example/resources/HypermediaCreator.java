package com.example.resources;

import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

//This class helps in applying HATEOAS constraint by created the hypermedia links
public class HypermediaCreator {
    private final UriInfo uriInfo;
    private List<Link> links;

    public HypermediaCreator(UriInfo uriInfo)
    {
        this.uriInfo = uriInfo;
        this.links = new ArrayList<>();
    }

    public HypermediaCreator baseUri(String path, String relation) {
        links.add(
                Link.fromUriBuilder(
                            uriInfo.getBaseUriBuilder().path(path)
                        )
                        .rel(relation)
                        .type("GET")
                        .build()
        );
        return this;
    }

    public HypermediaCreator requestUri(String path, String relation) {
        links.add(
                Link.fromUriBuilder(
                                uriInfo.getRequestUriBuilder().path(path)
                        )
                        .rel(relation)
                        .type("GET")
                        .build()
        );
        return this;
    }

    public List<Link> build(){
        List<Link> builtLinks = this.links;
        this.links = new ArrayList<>();
        return builtLinks;
    }

    public Link makeSelfLink(){
        return Link.fromUriBuilder(uriInfo.getAbsolutePathBuilder())
                .rel("self").build();
    }

}
