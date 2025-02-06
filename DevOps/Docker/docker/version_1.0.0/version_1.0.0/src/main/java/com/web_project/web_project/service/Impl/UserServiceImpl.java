package com.web_project.web_project.service.Impl;

import com.web_project.web_project.models.dto.offersDtos.CreateOfferDTO;
import com.web_project.web_project.models.dto.registrationDtos.CreateUserDTO;
import com.web_project.web_project.models.entity.BaseUser;
import com.web_project.web_project.models.entity.UserEntity;
import com.web_project.web_project.repository.UserRepository;
import com.web_project.web_project.service.OfferService;
import com.web_project.web_project.service.UserService;
import com.web_project.web_project.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final OfferService offerService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           ValidationUtil validationUtil,
                           OfferService offerService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.offerService = offerService;
    }

    @Override
    public void createUser(CreateUserDTO createUserDTO) {
        BaseUser user = new UserEntity()
                .setUsername(createUserDTO.getUsername())
                .setPassword(createUserDTO.getPassword())
                .setEmail(createUserDTO.getEmail())
                .setFirstName(createUserDTO.getFirstName())
                .setLastName(createUserDTO.getLastName())
                .setPhoneNumber(createUserDTO.getPhoneNumber());

        userRepository.save(user);
    }

    @Override
    public void createOffer(CreateOfferDTO createOfferDTO) {
        this.offerService.addOffer();

    }

    @Override
    public UserEntity findUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }


}
