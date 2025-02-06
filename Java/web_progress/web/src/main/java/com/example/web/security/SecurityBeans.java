package com.example.web.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityBeans {

  private final UserDetailsService userDetailsService;

  @Bean
  public AuthenticationProvider authenticationProvider(){
    log.info(" [ INFO ] LOADING SecurityBeans { authenticationProvider }");
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(this.userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    log.info(" [ INFO ] LOADING SecurityBeans { authenticationManager }");

    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    log.info(" [ INFO ] LOADING SecurityBeans { passwordEncoder }");
    return new BCryptPasswordEncoder();
  }

}
