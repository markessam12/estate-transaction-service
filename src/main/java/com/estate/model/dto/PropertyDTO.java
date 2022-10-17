package com.estate.model.dto;

import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.List;

@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyDTO {
    private int propertyId;
    private String address;
    private String propertyOwner;
    private long cost;
    private int forSale;
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;

    public PropertyDTO(){}

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPropertyOwner() {
        return propertyOwner;
    }

    public void setPropertyOwner(String propertyOwner) {
        this.propertyOwner = propertyOwner;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }


    public int getForSale() {
        return forSale;
    }

    public void setForSale(int forSale) {
        this.forSale = forSale;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
