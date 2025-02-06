package com.example.web.security.service;

import com.example.web.model.enums.RoleType;
import com.example.web.payload.request.AuthenticationRequestDTO;
import com.example.web.payload.request.UserRegistrationRequestDTO;
import com.example.web.payload.response.AuthenticationResponseDTO;
import com.example.web.payload.response.UserRegistrationResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {

  UserRegistrationResponseDTO registerUser(
      UserRegistrationRequestDTO userRegistrationDTO, RoleType user);

  ResponseEntity<AuthenticationResponseDTO> authenticate(AuthenticationRequestDTO authRequest);
}
