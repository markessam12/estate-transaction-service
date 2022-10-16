package com.example.model.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "property")
public class PropertyDTO {
    private int propertyId;
    private String address;
    private OwnerDTO propertyOwner;
    private long cost;
    private int forSale;

    public PropertyDTO(){}

    public PropertyDTO(String address, OwnerDTO propertyOwner, long cost) {
        this.address = address;
        this.propertyOwner = propertyOwner;
//        propertyOwner.addProperty(this);
        this.cost = cost;
    }

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

    public OwnerDTO getPropertyOwner() {
        return propertyOwner;
    }

    public void setPropertyOwner(OwnerDTO propertyOwner) {
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

}
