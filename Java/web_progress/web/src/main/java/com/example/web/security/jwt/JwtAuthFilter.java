package com.example.web.security.jwt;

import com.example.web.constant.JwtAuthFilterConstants;
import com.example.web.security.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    log.info("Loading JwtAuthFilter { doFilterInternal } ");

    final String authHeader = request.getHeader(JwtAuthFilterConstants.JWT_REQUEST_HEADER);
    final String jwtToken;
    final String userEmail;

    //user is not existing or is not logged in yet.
    if (authHeader == null || !authHeader.startsWith(JwtAuthFilterConstants.JWT_TOKEN_BEARER)) {
      filterChain.doFilter(request, response);
      return;
    }

    jwtToken = authHeader.substring(JwtAuthFilterConstants.JWT_TOKEN_BEARER.length());
    userEmail = this.jwtService.extractUsername(jwtToken);

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

      var isTokenValid = this.tokenRepository.findByToken(jwtToken)
          .map(t -> !t.isExpired() && !t.isRevoked())
          .orElse(false);

      //todo check if userDetails.getAuthorities() works fine
      if (jwtService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
            );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
