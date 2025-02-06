package com.example.web.security.jwt;

import io.jsonwebtoken.Claims;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

  String extractUsername(String jwtToken);

  String generateToken(UserDetails userDetails);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  Claims extractAllClaims(String jwtToken);

  boolean isTokenValid(String jwtToken, UserDetails userDetails);

  boolean isTokenExpired(String jwtToken);

  Date extractExpiration(String jwtToken);

  Key getSignInKey();
}
