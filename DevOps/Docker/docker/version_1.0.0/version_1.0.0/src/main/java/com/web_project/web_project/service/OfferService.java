package com.web_project.web_project.service;

import com.web_project.web_project.models.dto.offersDtos.CreateOfferDTO;
import com.web_project.web_project.models.dto.offersDtos.OfferByIdDTO;
import com.web_project.web_project.models.entity.OfferEntity;

import java.util.Optional;

public interface OfferService {

    void createOffer(CreateOfferDTO createOfferDTO);

    OfferEntity findById(OfferByIdDTO offerByIdDTO);

    void addOffer(CreateOfferDTO createOfferDTO);

    void updateOffer(CreateOfferDTO createOfferDTO);
}
