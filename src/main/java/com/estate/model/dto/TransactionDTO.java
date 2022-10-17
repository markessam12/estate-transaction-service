package com.estate.model.dto;

import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.List;

@XmlRootElement(name = "transaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionDTO {
    private String date;
    private String seller;
    private String buyer;
    private int property;
    private long price;
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> links;


    public TransactionDTO(){}
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

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
