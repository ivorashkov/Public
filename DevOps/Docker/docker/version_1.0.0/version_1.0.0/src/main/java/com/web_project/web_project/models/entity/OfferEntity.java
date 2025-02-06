package com.web_project.web_project.models.entity;

import com.web_project.web_project.models.enums.TransportType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
public class OfferEntity extends BaseEntity{

    private String country;
    private String destination;
    private String hotelName;
    private String stars;
    private String duration;
    private TransportType transportType;
    private BigDecimal price;
    private BigDecimal discount;
    private String description;
    private LocalDateTime timeOfCreation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public OfferEntity() {
        this.timeOfCreation = LocalDateTime.now();
    }

    public OfferEntity(
            String country,
            String destination,
            String hotelName,
            String stars,
            String duration,
            TransportType transportType,
            BigDecimal price,
            BigDecimal discount,
            String description,
            UserEntity user
    ) {
        this();
        this.setCountry(country);
        this.setDestination(destination);
        this.setHotelName(hotelName);
        this.setStars(stars);
        this.setDuration(duration);
        this.setTransportType(transportType);
        this.setPrice(price);
        this.setDiscount(discount);
        this.setDescription(description);
        this.setUser(user);
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getCountry() {
        return country;
    }

    public OfferEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getDestination() {
        return this.destination;
    }

    public OfferEntity setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getHotelName() {
        return this.hotelName;
    }

    public OfferEntity setHotelName(String hotelName) {
        this.hotelName = hotelName;
        return this;
    }

    public String getStars() {
        return this.stars;
    }

    public OfferEntity setStars(String stars) {
        this.stars = stars;
        return this;
    }

    public String getDuration() {
        return this.duration;
    }

    public OfferEntity setDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public TransportType getTransportType() {
        return this.transportType;
    }

    public OfferEntity setTransportType(TransportType transportType) {
        this.transportType = transportType;
        return this;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public OfferEntity setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public OfferEntity setDiscount(BigDecimal discount) {
        this.discount = discount;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public OfferEntity setDescription(String description) {
        this.description = description;
        return this;
    }

}