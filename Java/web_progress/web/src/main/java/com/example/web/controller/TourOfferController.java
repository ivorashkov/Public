package com.example.web.controller;

import com.example.web.model.requestDto.TourOfferCreateRequestDTO;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.requestDto.TourOfferEditRequestDTO;
import com.example.web.model.responseDTO.TourOfferCreateResponseDTO;
import com.example.web.model.responseDTO.TourOfferShortResponseDTO;
import com.example.web.security.jwt.JwtService;
import com.example.web.service.FileService;
import com.example.web.service.TourOfferFilePathService;
import com.example.web.service.TourOfferService;
import com.example.web.service.UserService;
import com.example.web.util.ValidatorUtil;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/offer")
public class TourOfferController {

  private final UserService userService;
  private final FileService fileService;
  private final TourOfferFilePathService tourOfferDataService;
  private final TourOfferService tourOfferService;

  private final ValidatorUtil validatorUtil;

  @GetMapping("/all/{id}")
  public ResponseEntity<List<TourOfferShortResponseDTO>> getAllForUser(
      @PathVariable(name = "id") Long userId) {

    return this.validatorUtil.responseEntityList(this.tourOfferService.findAllByUserId(userId));
  }

  @PatchMapping("/edit")
  public ResponseEntity<TourOfferFullDTO> editOffer(
      @RequestParam(name = "offerId", required = true) Long offerId,
      @RequestParam(name = "userId", required = true) Long userId

  ) {

    TourOfferFullDTO tourOfferFullDTO =
        this.tourOfferService.findByIdAndUserId(offerId, userId);

    /** http://localhost:8091/offer/edit?offerId=1&userId=1 */
    return this.validatorUtil.responseEntity(tourOfferFullDTO);
  }

  @PatchMapping("/save")
  public ResponseEntity<TourOfferFullDTO> saveOffer(
//      @RequestParam(name = "offerId") Long offerId,
//      @RequestParam(name = "userId") Long userId,
//      @RequestBody TourOfferFullDTO offerDTO
      @RequestPart(value = "file", required = false) List<MultipartFile> files
  ) {
//TO BE TESTED
    //todo да говоря с кольо дали ще ми подава директно JSON обект или допълнително offerId, userId
    //TODO TO FINISH, HOW TO RECEIVE JSON OR PROCESS IT CORRECTLY

    //1. Получаваме новия обект
    TourOfferFullDTO incoming = this.tourOfferService.findByIdAndUserId(7L, 3L);
    UserDTO userDTO = this.userService.findUserDTOById(3L);
    TourOfferEditRequestDTO editDTO = new TourOfferEditRequestDTO
        (
            incoming.getId(),
            userDTO,
            incoming.getTitle(),
            incoming.getCountry(),
            incoming.getCity(),
            incoming.getDuration(),
            incoming.getStars(),
            incoming.getPrice(),
            incoming.getDescription(),
            incoming.getDiscount(),
            incoming.getTransportType()
        );

    //2. намираме обекта в базата
    TourOfferFullDTO offerDTO =
        this.tourOfferService.findByIdAndUserId(editDTO.getId(), editDTO.getUser().getId());

    offerDTO = this.tourOfferService.setNewProperties(editDTO, offerDTO);

    //set files to delete in DB
    this.tourOfferService.deleteOfferFilePaths(offerDTO);

    //save new files
    this.fileService.handleAllFilesUpload(files, offerDTO.getUser().getId(), offerDTO.getId());

    //set new files
    offerDTO.setPaths(this.tourOfferDataService.getOfferPaths(offerDTO));

    //response
    return this.validatorUtil.responseEntity(offerDTO);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<Object> deleteOffer(
      @RequestParam("userId") Long userId,
      @RequestParam("offerId") Long offerId
  ) {

    //localhost:8091/offer/delete?userId=3&offerId=6
    return this.validatorUtil.responseEntityBoolean(
        this.tourOfferService.deleteOffer(userId, offerId));
  }

  @PostMapping(value = "/create")
  public ResponseEntity<TourOfferCreateResponseDTO> createOffer(
      @RequestBody TourOfferCreateRequestDTO createRequestDTO,
//      @RequestPart List<MultipartFile> files,
      @RequestParam(name = "userId") Long id
  ) {
    log.info("[ INFO ] Loading TourOfferController /api/offer/create/ { createOffer }");

    return this.validatorUtil.responseEntity(
        this.tourOfferService.createOffer(
            id,
            createRequestDTO
        )
    );
  }

  @PostMapping("/finish")
  public ResponseEntity<TourOfferFullDTO> finishOfferCreation(
      @RequestParam(name = "userId") Long userId,
      @RequestParam(name = "offerId") Long offerId,
      @RequestPart("file") List<MultipartFile> files
  ) {
    log.info("[ INFO ] Loading TourOfferController /api/offer/finish/ { finishOfferCreation }");

    TourOfferFullDTO tourOfferFullDTO = this.tourOfferService.saveOfferAndPath(userId, offerId);

    this.fileService.handleAllFilesUpload(
        files,
        tourOfferFullDTO.getUser().getId(),
        tourOfferFullDTO.getId()
    );

    tourOfferFullDTO.setPaths(this.tourOfferDataService.getOfferPaths(tourOfferFullDTO));

    return this.validatorUtil.responseEntity(tourOfferFullDTO);
  }
}
