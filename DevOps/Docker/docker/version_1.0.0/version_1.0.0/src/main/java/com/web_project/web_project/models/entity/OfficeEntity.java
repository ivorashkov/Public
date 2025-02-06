package com.web_project.web_project.models.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "offices")
public class OfficeEntity extends BaseEntity {

    private String address;
    private String city;
    private String country;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public OfficeEntity() {
    }

    public OfficeEntity(String address, String city, String country, UserEntity user) {
        this.address = address;
        this.city = city;
        this.country = country;
        this.user = user;
    }

    public String getCountry() {
        return country;
    }

    public OfficeEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getAddress() {
        return this.address;
    }

    public OfficeEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return this.city;
    }

    public OfficeEntity setCity(String city) {
        this.city = city;
        return this;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}