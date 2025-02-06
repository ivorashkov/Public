package com.example.web.controller;

import com.example.web.model.enums.RoleType;
import com.example.web.payload.request.AuthenticationRequestDTO;
import com.example.web.payload.request.UserRegistrationRequestDTO;
import com.example.web.payload.response.AuthenticationResponseDTO;
import com.example.web.security.service.AuthService;
import com.example.web.util.ValidatorUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final ValidatorUtil validatorUtil;
  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser
      (@Valid @RequestBody UserRegistrationRequestDTO registrationDTO) {

    return this.validatorUtil.responseEntity(
        this.authService.registerUser(registrationDTO, RoleType.INACTIVE));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponseDTO> authenticate
      (@Valid @RequestBody AuthenticationRequestDTO authRequest) {

    return this.authService.authenticate(authRequest);
  }
}
