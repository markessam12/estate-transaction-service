package com.estate.model.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type PropertyDTO is the property data transfer object.
 * It is the object that's used between service layer and controller layer.
 */
@XmlRootElement(name = "property")
public class PropertyDTO extends HATEOAS{
    private int propertyId;
    private String address;
    private String propertyOwner;
    private long cost;
    private int forSale;

    /**
     * Instantiates a new Property dto.
     */
    public PropertyDTO(){}

    /**
     * Gets property id.
     *
     * @return the property id
     */
    public int getPropertyId() {
        return propertyId;
    }

    /**
     * Sets property id.
     *
     * @param propertyId the property id
     */
    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets property owner.
     * Unlike in DAO, this attribute in DTO refers only to the username of the owner, not the owner object.
     *
     * @return the property owner
     */
    public String getPropertyOwner() {
        return propertyOwner;
    }

    /**
     * Sets property owner.
     * Unlike in DAO, this attribute in DTO refers only to the username of the owner, not the owner object.
     *
     * @param propertyOwner the property owner
     */
    public void setPropertyOwner(String propertyOwner) {
        this.propertyOwner = propertyOwner;
    }

    /**
     * Gets cost.
     *
     * @return the cost
     */
    public long getCost() {
        return cost;
    }

    /**
     * Sets cost.
     *
     * @param cost the cost
     */
    public void setCost(long cost) {
        this.cost = cost;
    }


    /**
     * Gets for sale.
     *
     * @return the for sale
     */
    public int getForSale() {
        return forSale;
    }

    /**
     * Sets for sale.
     *
     * @param forSale the for sale
     */
    public void setForSale(int forSale) {
        this.forSale = forSale;
    }
}
