package com.estate.model.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type TransactionDTO is the transaction data transfer object.
 * It is the object that's used between service layer and controller layer.
 */
@XmlRootElement(name = "transaction")
public class TransactionDTO extends HATEOAS{
    private String date;
    private String seller;
    private String buyer;
    private int property;
    private long price;

    /**
     * Instantiates a new Transaction dto.
     */
    public TransactionDTO(){}

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets seller.
     *
     * @return the seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Sets seller.
     * Unlike in DAO, this attribute in DTO refers only to the username of the owner, not the owner object.
     *
     * @param seller the seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * Gets buyer.
     * Unlike in DAO, this attribute in DTO refers only to the username of the owner, not the owner object.
     *
     * @return the buyer
     */
    public String getBuyer() {
        return buyer;
    }

    /**
     * Sets buyer.
     * Unlike in DAO, this attribute in DTO refers only to the username of the owner, not the owner object.
     *
     * @param buyer the buyer
     */
    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    /**
     * Gets property.
     * Unlike in DAO, this attribute in DTO refers only to the username of the owner, not the owner object.
     *
     * @return the property
     */
    public int getProperty() {
        return property;
    }

    /**
     * Sets property.
     * Unlike in DAO, this attribute in DTO refers only to the unique id of the property, not the property object.
     *
     * @param property the property
     */
    public void setProperty(int property) {
        this.property = property;
    }

    /**
     * Gets price.     * Unlike in DAO, this attribute in DTO refers only to the unique id of the property, not the property object.
     * Unlike in DAO, this attribute in DTO refers only to the unique id of the property, not the property object.
     *
     * @return the price
     */
    public long getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(long price) {
        this.price = price;
    }
}
