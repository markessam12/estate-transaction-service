package com.example.model.dto;

import com.example.util.Utility;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transaction")
public class TransactionDTO {
    private int transactionId;
    private String date;
    private String seller;
    private String buyer;
    private int property;
    private long price;

    public TransactionDTO(){}

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

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
