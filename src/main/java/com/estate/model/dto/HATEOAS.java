package com.estate.model.dto;

import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an abstract class for applying Rest HATEOAS Constraint.
 * It stores links for easily accessing other resources.
 *  HATEOAS stands for "Hypermedia As The Engine Of Application State"
 */
public abstract class HATEOAS {
    /**
     * The list of hypermedia links.
     */
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    public List<Link> links;

    /**
     * Add link to links list.
     *
     * @param link the link
     */
    public void addLink(Link link){
        if(links == null)
            this.links = new ArrayList<>();
        this.links.add(link);
    }
}
