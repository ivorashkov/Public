package com.example.web.security.service.impl;

import com.example.web.constant.JwtAuthFilterConstants;
import com.example.web.security.repository.TokenRepository;
import com.example.web.security.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class LogoutServiceImpl implements LogoutService {
  private final TokenRepository tokenRepository;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {

    final String authHeader = request.getHeader(JwtAuthFilterConstants.JWT_REQUEST_HEADER);
    final String jwtToken;

    if (authHeader == null || !authHeader.startsWith(JwtAuthFilterConstants.JWT_TOKEN_BEARER)) {
      return;
    }
    jwtToken = authHeader.substring(JwtAuthFilterConstants.JWT_TOKEN_BEARER.length());
    var storedToken = this.tokenRepository.findByToken(jwtToken)
        .orElse(null);

    if (storedToken != null){
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      this.tokenRepository.save(storedToken);
    }
  }
}
