package com.example.web.controller;

import com.example.web.model.dto.UserDTO;
import com.example.web.model.requestDto.UserEditRequestDTO;
import com.example.web.model.responseDTO.UserEditResponseDTO;
import com.example.web.service.UserService;
import com.example.web.util.ValidatorUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final ValidatorUtil validatorUtil;
  private final UserService userService;

  @GetMapping("user/{id}")
  public ResponseEntity<UserDTO> response(@PathVariable("id") @Valid Long userId) {
    return this.validatorUtil.responseEntity(userService.findUserDTOById(userId));
  }

  @GetMapping("/edit")
  public ResponseEntity<UserEditResponseDTO> edit(
      @RequestBody @Valid UserEditRequestDTO userEditRequestDTO) {

    return this.validatorUtil.responseEntity(
        this.userService.findUserDTOByEmail(userEditRequestDTO));
  }

  @PatchMapping("/save")
  public ResponseEntity<UserEditResponseDTO> save(
      @RequestBody @Valid UserEditResponseDTO userEditRequestDTO) {

    return this.validatorUtil.responseEntity(
        this.userService.save(userEditRequestDTO));
  }

}
