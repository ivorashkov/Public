package com.web_project.web_project.service;

import com.web_project.web_project.models.dto.offersDtos.CreateOfferDTO;
import com.web_project.web_project.models.dto.registrationDtos.CreateUserDTO;
import com.web_project.web_project.models.entity.UserEntity;

import java.util.Optional;

public interface UserService {

    void createUser(CreateUserDTO createUserDTO);

    void createOffer(CreateOfferDTO createOfferDTO);

    UserEntity findUserById(Long id);

}
