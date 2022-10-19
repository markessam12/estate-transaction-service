package com.estate.model.dto;

import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class HATEOAS {
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;

    public void addLink(Link link){
        if(links == null)
            this.links = new ArrayList<>();
        this.links.add(link);
    }
}
