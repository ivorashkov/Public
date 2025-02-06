package com.example.web.controller;

import com.example.web.model.dto.TourOfferCreateDTO;
import com.example.web.model.dto.TourOfferImagePathDTO;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.enums.TransportType;
import com.example.web.service.FileService;
import com.example.web.service.TourOfferDataService;
import com.example.web.service.TourOfferService;
import com.example.web.service.UserService;
import com.example.web.util.ValidatorUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@AllArgsConstructor
@RequestMapping("/offer")
public class TourOfferController {

  private final UserService userService;
  private final FileService fileService;
  private final TourOfferDataService tourOfferDataService;
  private final TourOfferService tourOfferService;

  private final ValidatorUtil validatorUtil;

  @PatchMapping("/edit")
  public ResponseEntity<TourOfferFullDTO> editOffer(
      @RequestParam(name = "offerId", required = true) Long offerId,
      @RequestParam(name = "userId", required = true) Long userId
  ) {

//    List<TourOfferImagePathDTO> offerDataPathDTO = this.tourOfferDataService.findAllOfferDataPaths(
//        offerId);
    TourOfferFullDTO tourOfferFullDTO =
        this.tourOfferService.getOfferWithPathsAndUsersDTOs(offerId, userId);

    /** http://localhost:8091/offer/edit?offerId=1&userId=1 */
    return this.validatorUtil.responseEntity(tourOfferFullDTO);
  }

  @PostMapping("/save")
  public ResponseEntity<TourOfferFullDTO> saveOffer(
      @RequestParam(name = "offerId") Long offerId,
      @RequestParam(name = "userId") Long userId,
      @RequestBody TourOfferFullDTO offerDTO
  ) {

    //todo да говоря с кольо дали ще ми подава директно JSON обект или допълнително offerId, userId
    //TODO TO FINISH, HOW TO RECEIVE JSON OR PROCESS IT CORRECTLY

    TourOfferFullDTO offerDTO1 = offerDTO;
    return null;
  }

  @DeleteMapping("/delete")
  public void deleteOffer(
      @RequestParam("userId") Long userId,
      @RequestParam("offerId") Long offerId
  ) {

    //localhost:8091/offer/delete?userId=1&offerId=1
    this.tourOfferService.deleteOffer(userId, offerId);
  }

  @PostMapping(value = "/create", consumes = {"multipart/form-data"})
  public ResponseEntity<TourOfferFullDTO> createOffer(
      @RequestPart(value = "file", required = false) List<MultipartFile> files
      //  ,@RequestPart("json") TourOfferCreateDTO tourOfferCreateDTO
      //,@RequestBody @Valid TourOfferCreateDTO tourOfferCreateDTO
  ) {

//https://stackoverflow.com/questions/49845355/spring-boot-controller-upload-multipart-and-json-to-dto

    /** incoming JSON object ImportCreateOfferInfoDTO */

    UserDTO userDTO = this.userService.findUserDTOById(3L);

    TourOfferCreateDTO createOfferDTO = new TourOfferCreateDTO
        (
            "new Title",
            LocalDateTime.now(),
            "Testoniq",
            "Testanqn",
            5,
            4.0,
            BigDecimal.valueOf(110.11),
            "testche",
            0,
            userDTO,
            TransportType.airplane
        );

    /** DTO + FILES **/
    TourOfferFullDTO tourOfferFullDTO = this.tourOfferService.saveOfferAndPath(createOfferDTO);

    this.fileService.handleAllFilesUpload(files, tourOfferFullDTO);

    tourOfferFullDTO.setPaths(this.tourOfferDataService.getOfferPaths(tourOfferFullDTO));

    return this.validatorUtil.responseEntity(tourOfferFullDTO);
  }

  @GetMapping("/test")
  public TourOfferCreateDTO sendDTO() {
    UserDTO user = this.userService.findUserDTOById(3L);

    return new TourOfferCreateDTO(

        "new Title",
        LocalDateTime.now(),
        "Testoniq",
        "Testanqn",
        5,
        4.0,
        BigDecimal.valueOf(11.11),
        "testche",
        0,
        user,
        TransportType.airplane);
  }


}
