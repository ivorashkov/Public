package com.example.web.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

  //todo move it to application properties -> 512 bit key
  @Value("${jwt.secret.key}")
  private String SECRET_KEY;

  @Override
  public String extractUsername(String jwtToken) {
    log.info("[ INFO ] Loading JwtServiceImpl { extractUsername } ");
    return extractClaim(jwtToken, Claims::getSubject);
  }
  public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
    log.info("[ INFO ] Loading JwtServiceImpl { extractClaim } ");
    final Claims claims = extractAllClaims(jwtToken);

    return claimsResolver.apply(claims);
  }

  @Override
  public String generateToken(UserDetails userDetails) {
    log.info("[ INFO ] Loading JwtServiceImpl { generateToken } ");
    //generate no extra claims since the HashMap is empty
    return generateToken(new HashMap<>(), userDetails);
  }

  @Override
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    log.info("[ INFO ] Loading JwtServiceImpl { generateToken } ");
    //adding extra claims with this method
    return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        //todo add admin/user role as claim maybe??
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public Claims extractAllClaims(String jwtToken) {
    log.info("[ INFO ] Loading JwtServiceImpl { extractAllClaims } ");

    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(jwtToken)
        .getBody();
  }

  @Override
  public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
    log.info("[ INFO ] Loading JwtServiceImpl { isTokenValid } ");
    //validate if token is the same within the user.
    final String username = extractUsername(jwtToken);

    return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwtToken);
  }

  @Override
  public boolean isTokenExpired(String jwtToken) {
    log.info("[ INFO ] Loading JwtServiceImpl { isTokenExpired } ");

    return extractExpiration(jwtToken).before(new Date());
  }

  @Override
  public Date extractExpiration(String jwtToken) {
    log.info("[ INFO ] Loading JwtServiceImpl { extractExpiration } ");

    return extractClaim(jwtToken, Claims::getExpiration);
  }

  @Override
  public Key getSignInKey() {
    log.info("[ INFO ] Loading JwtServiceImpl { getSignInKey } ");

    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
