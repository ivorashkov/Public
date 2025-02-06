package com.example.web.security.service.impl;

import com.example.web.constant.UserRegistrationConstants;
import com.example.web.exception.UserNotFoundException;
import com.example.web.model.entity.UserEntity;
import com.example.web.model.enums.RoleType;
import com.example.web.payload.request.AuthenticationRequestDTO;
import com.example.web.payload.request.UserRegistrationRequestDTO;
import com.example.web.payload.response.AuthenticationResponseDTO;
import com.example.web.payload.response.UserRegistrationResponseDTO;
import com.example.web.repository.RoleTypeRepository;
import com.example.web.repository.UserRepository;
import com.example.web.security.CustomUserDetails;
import com.example.web.security.jwt.JwtService;
import com.example.web.security.repository.TokenRepository;
import com.example.web.security.service.AuthService;
import com.example.web.security.token.TokenEntity;
import com.example.web.security.token.TokenType;
import com.example.web.util.ValidatorUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final ValidatorUtil validatorUtil;

  private final RoleTypeRepository roleTypeRepository;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  public UserRegistrationResponseDTO registerUser
      (UserRegistrationRequestDTO registrationDTO, RoleType roleType) {
    log.info("[ INFO ] Loading method registerUser in AuthServiceImpl");

    //todo check what info this method is giving back
    try {
      if (this.userRepository.existsByEmail(registrationDTO.getEmail())) {
        log.error("[ ERROR ] While Loading AuthServiceImpl { registerUser } email already exists ");
        //email exists
        return null;
      }

      if (!registrationDTO.getPassword().equals(registrationDTO.getPasswordConfirm())) {
        log.error("[ ERROR ] While Loading AuthServiceImpl{registerUser} passwords are not equal");
        //passwords are not equal
        return null;
      }

      if (this.userRepository.existsByPhoneNumber(registrationDTO.getPhoneNumber())){
        log.error("[ ERROR ] Phone Number already exist");
        return null;
      }

      var user = setNewUserFields(registrationDTO);

      switch (roleType) {
        case ADMIN -> user.setRole(this.roleTypeRepository.findByRoleName(RoleType.ADMIN));
        default -> user.setRole(this.roleTypeRepository.findByRoleName(RoleType.INACTIVE));
      }
      userRepository.saveAndFlush(user);

      return this.validatorUtil.getDTOFromEntity(user, UserRegistrationResponseDTO.class);

    } catch (Exception e) {
      log.error("[ ERROR ] WHILE LOADING registerUser in AuthServiceImpl {}]", e.getMessage());
      return null;
    }

  }

  @Override
  public ResponseEntity<AuthenticationResponseDTO> authenticate(
      AuthenticationRequestDTO authRequest) {
    log.info("[ INFO ] Loading method authenticate in AuthServiceImpl");
    //todo check what info this method is giving back
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            authRequest.getEmail(),
            authRequest.getPassword()
        )
    );

    //user is correct at this point
    var user = this.userRepository.findUserEntityByEmail(authRequest.getEmail())
        .orElseThrow(UserNotFoundException::new);

    Map<String, Object> extraClaims = getExtraClaims(user);

    var jwtToken = this.jwtService.generateToken(extraClaims, new CustomUserDetails(user));

    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);

    AuthenticationResponseDTO response = AuthenticationResponseDTO.builder()
        .token(jwtToken)
        .build();

    return this.validatorUtil.responseEntity(response);
  }

  private Map<String, Object> getExtraClaims(UserEntity user) {
    log.info("[ INFO ] Loading AuthServiceImpl  { getExtraClaims }");
    //todo here we can add additional claims
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("id", user.getId());
    extraClaims.put("status", user.getRole().getRoleName());

    return extraClaims;
  }

  private void saveUserToken(UserEntity user, String jwtToken) {
    log.info("[ INFO ] Loading AuthServiceImpl  { saveUserToken }");

    var token = TokenEntity.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .revoked(false)
        .expired(false)
        .build();
    this.tokenRepository.save(token);
  }

  public void revokeAllUserTokens(UserEntity user){
    var validTokens = this.validatorUtil.getListFromOptionalList(
        this.tokenRepository.findAllByUserIdAndExpiredIsFalseAndRevokedIsFalse(user.getId())
    );

    if (validTokens.isEmpty()){
      return;
    }

    validTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });

    tokenRepository.saveAll(validTokens);
  }

  private UserEntity setNewUserFields(UserRegistrationRequestDTO registrationDTO) {
    log.info("[ INFO ] Loading method setNewUserFields in AuthServiceImpl");

    return UserEntity.builder()
        .firstName(registrationDTO.getFirstName())
        .lastName(registrationDTO.getLastName())
        .email(registrationDTO.getEmail())
        .password(passwordEncoder.encode(registrationDTO.getPassword()))
        .phoneNumber(registrationDTO.getPhoneNumber())
        .approvedBy(UserRegistrationConstants.APPROVED_BY_INITIAL_ID)
        .hasStar(UserRegistrationConstants.HAS_NO_STAR)
        .build();
  }
}
