package com.example.model.dto;

import com.example.util.Utility;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transaction")
public class TransactionDTO {
    private int transactionId;
    private String date;
    private OwnerDTO seller;
    private OwnerDTO buyer;
    private PropertyDTO property;
    private long price;

    public TransactionDTO(){}

    public TransactionDTO(OwnerDTO seller, OwnerDTO buyer, PropertyDTO property) {
        this.date = Utility.getCurrectDate();
        this.seller = seller;
        this.buyer = buyer;
        this.property = property;
        this.price = property.getCost();
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OwnerDTO getSeller() {
        return seller;
    }

    public void setSeller(OwnerDTO seller) {
        this.seller = seller;
    }

    public OwnerDTO getBuyer() {
        return buyer;
    }

    public void setBuyer(OwnerDTO buyer) {
        this.buyer = buyer;
    }

    public PropertyDTO getProperty() {
        return property;
    }

    public void setProperty(PropertyDTO property) {
        this.property = property;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
