package com.example.web.controller;

import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.service.FileService;
import com.example.web.service.TourOfferService;
import com.example.web.service.UserService;
import com.example.web.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class FileController {

  private final FileService fileService;
  private final UserService userService;
  private final TourOfferService tourOfferService;
  private final ValidatorUtil validatorUtil;

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> response(@PathVariable("id") Long userId) {
    return this.validatorUtil.responseEntity(userService.findUserDTOById(userId));
  }

  @PatchMapping("/upload/all")
  public String handleAllFilesUpload(
      @RequestParam("file") List<MultipartFile> files,
      @RequestParam(name = "userId") Long userId,
      @RequestParam(name = "offerId", defaultValue = "-1") Long offerId
  ) {
    //TODO NOT STRING BUT RESPONSE CODE SHOULD BE RETURNED FROM handleAllFilesUpload
    //TODO ResponseEntity
    //TODO return new ResponseEntity(HttpStatus.NOT_FOUND);
    //localhost:8091/user/upload/all?userId=1&offerId=3
    //localhost:8091/user/upload/all?userId=1
    UserDTO userDTO = this.userService.findUserDTOById(userId);
    TourOfferFullDTO offerDTO;
    if (offerId > 0) {
      offerDTO = this.tourOfferService.findById(offerId, userDTO);
      this.fileService.handleAllFilesUpload(files, offerDTO);
      return "All files of the offer are saved";
    }

    this.fileService.handleAllFilesUpload(files, userId, offerId, userDTO);

    return "All files are saved for user " + userDTO.getUsername();
  }
}
