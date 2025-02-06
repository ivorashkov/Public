package com.web_project.web_project.models.dto.offersDtos;

import com.web_project.web_project.models.enums.TransportType;

import java.math.BigDecimal;

public class CreateOfferDTO {

    private String country;
    private String destination;
    private String hotelName;
    private String stars;
    private String duration;
    private TransportType transportType;
    private BigDecimal price;
    private BigDecimal discount;
    private String description;
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public CreateOfferDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public CreateOfferDTO setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getDestination() {
        return destination;
    }

    public CreateOfferDTO setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getHotelName() {
        return hotelName;
    }

    public CreateOfferDTO setHotelName(String hotelName) {
        this.hotelName = hotelName;
        return this;
    }

    public String getStars() {
        return stars;
    }

    public CreateOfferDTO setStars(String stars) {
        this.stars = stars;
        return this;
    }

    public String getDuration() {
        return duration;
    }

    public CreateOfferDTO setDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public CreateOfferDTO setTransportType(TransportType transportType) {
        this.transportType = transportType;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public CreateOfferDTO setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public CreateOfferDTO setDiscount(BigDecimal discount) {
        this.discount = discount;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateOfferDTO setDescription(String description) {
        this.description = description;
        return this;
    }
}
