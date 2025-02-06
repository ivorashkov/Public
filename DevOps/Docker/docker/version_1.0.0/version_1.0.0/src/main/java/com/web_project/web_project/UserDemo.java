package com.web_project.web_project;

import com.web_project.web_project.models.dto.offersDtos.CreateOfferDTO;
import com.web_project.web_project.models.dto.registrationDtos.CreateUserDTO;
import com.web_project.web_project.models.enums.TransportType;
import com.web_project.web_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserDemo implements CommandLineRunner {

    private UserService userService;

    @Autowired
    public UserDemo(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        userService.createUser(
                new CreateUserDTO()
                        .setUsername("test1")
                        .setPassword("1234")
                        .setEmail("test@abv.bg")
                        .setFirstName("ivaylo")
                        .setLastName("rashkov")
                        .setPhoneNumber("2103124412"));

        userService.createOffer(
                new CreateOfferDTO()
                        .setCountry("Bulgaria")
                        .setDestination("Sofia")
                        .setHotelName("Plaza")
                        .setStars("5")
                        .setDuration("7 dni")
                        .setTransportType(TransportType.BUS)
                        .setPrice(BigDecimal.valueOf(254,50))
                        .setDiscount(BigDecimal.valueOf(0))
                        .setDescription("nqma kak da stane")
                        .setUserId(1L)
        );

    }
}
