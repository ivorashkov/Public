package com.web_project.web_project.models.entity;

import com.web_project.web_project.models.dto.offersDtos.CreateOfferDTO;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "users")
public class UserEntity extends BaseUser {

    private String phoneNumber;
    // private ImagePlus idFrontPicture;
    // private ImagePlus idBackPicture;
    // private ImagePlus selfieForApproval;

    private String credentialNumber;
    private String companyName;
    private String uniqueIdentifier;
    private String addressRegistration;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user"
    )
    private List<OfficeEntity> offices;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user"
    )
    private List<OfferEntity> offers;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            targetEntity = UserRoleEntity.class
    )
    private UserRoleEntity userRoleEntity;

    public List<OfferEntity> getOffers() {
        return offers;
    }

    public void addOffer(OfferEntity offer) {

    }

    public UserEntity() {
        this.offices = new ArrayList<>();
        this.offers = new ArrayList<>();
    }

    public UserEntity(String phoneNumber, String credentialNumber,
                      String companyName, String uniqueIdentifier,
                      String addressRegistration, List<OfficeEntity> offices,
                      List<OfferEntity> offers, UserRoleEntity userRoleEntity) {

        this.setPhoneNumber(phoneNumber);
        this.setCredentialNumber(credentialNumber);
        this.setCompanyName(companyName);
        this.setUniqueIdentifier(uniqueIdentifier);
        this.setAddressRegistration(addressRegistration);
        this.setOffices(offices);
        this.setOffers(offers);
        this.setUserRoleEntity(userRoleEntity);

    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public UserEntity setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UserEntity setOffers(List<OfferEntity> offers) {
        this.offers = offers;
        return this;
    }

    public String getCredentialNumber() {
        return credentialNumber;
    }

    public void setCredentialNumber(String credentialNumber) {
        this.credentialNumber = credentialNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getAddressRegistration() {
        return addressRegistration;
    }

    public void setAddressRegistration(String addressRegistration) {
        this.addressRegistration = addressRegistration;
    }

    public List<OfficeEntity> getOffices() {
        return offices;
    }

    public void setOffices(List<OfficeEntity> offices) {
        this.offices = offices;
    }

    public UserRoleEntity getUserRoleEntity() {
        return userRoleEntity;
    }

    public void setUserRoleEntity(UserRoleEntity userRoleEntity) {
        this.userRoleEntity = userRoleEntity;
    }

}
