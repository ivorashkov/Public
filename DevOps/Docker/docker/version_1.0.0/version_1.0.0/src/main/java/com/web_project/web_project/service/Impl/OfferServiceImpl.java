package com.web_project.web_project.service.Impl;

import com.web_project.web_project.models.dto.offersDtos.CreateOfferDTO;
import com.web_project.web_project.models.dto.offersDtos.OfferByIdDTO;
import com.web_project.web_project.models.entity.OfferEntity;
import com.web_project.web_project.repository.OfferRepository;
import com.web_project.web_project.repository.UserRepository;
import com.web_project.web_project.service.OfferService;
import com.web_project.web_project.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,
                            UserRepository userRepository, ModelMapper modelMapper,
                            ValidationUtil validationUtil
    ) {
        this.offerRepository = offerRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public void createOffer(CreateOfferDTO createOfferDTO) {
        //todo
    }

    @Override
    public OfferEntity findById(OfferByIdDTO offerByIdDTO) {
        return this.offerRepository.findById(offerByIdDTO.getId()).orElse(null);
    }

    @Override
    public void addOffer(CreateOfferDTO createOfferDTO,) {
        this.offerRepository.save(new OfferEntity()
                .setCountry(createOfferDTO.getCountry())
                .setDestination(createOfferDTO.getDestination())
                .setHotelName(createOfferDTO.getHotelName())
                .setStars(createOfferDTO.getStars())
                .setDuration(createOfferDTO.getDuration())
                .setTransportType(createOfferDTO.getTransportType())
                .setPrice(createOfferDTO.getPrice())
                .setDiscount(createOfferDTO.getDiscount())
                .setDescription(createOfferDTO.getDescription())
                .setUser());
    }

    @Override
    public void updateOffer(CreateOfferDTO createOfferDTO) {

    }
}
