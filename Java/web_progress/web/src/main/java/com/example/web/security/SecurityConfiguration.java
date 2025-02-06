package com.example.web.security;

import static org.springframework.web.cors.CorsConfiguration.ALL;

import com.example.web.model.enums.RoleType;
import com.example.web.security.jwt.JwtAuthFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

//  private final JwtAuthFilter jwtAuthFilter;
//  //  private final CorsFilter corsFilter;
//  private final AuthenticationProvider authenticationProvider;
//  private final LogoutHandler logoutHandler;//LogoutService implimentation. Spring will find that we have impl

  private static final String[] AUTH_WHITELIST = {
      "/api/home/offers",
      "/api/home/country/list",
      "/api/home/city/list",
      "/api/home/{id}",
      "/api/auth/signup",
      "/api/auth/authenticate",
      "/api/auth/logout",
      "/api/user/upload/all/*",
      "/v3/api-docs/**",
      "/swagger-ui/**",
      "/public/**"
  };

  private static final String[] AUTH_ADMIN_LIST = {
      "/api/admin/request"
  };

  private static final String[] AUTH_ACTIVE_USER_LIST = {
      "/api/user/upload/all",
      "/api/offer/save",
      "/api/offer/edit",
      "/api/offer/delete",
      "/api/offer/create",
      "/api/offer/finish"
  };

  private static final String LOGOUT_URL = "/api/auth/logout";

  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers(AUTH_WHITELIST)
            .permitAll()
            .anyRequest()
            .authenticated())
        .addFilterBefore()
  }

//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//    //first step disable csrf
//    return httpSecurity
//        .csrf().and().cors().disable()
//        //authorize HTTP REQUESTS
//        .authorizeHttpRequests(authorize ->
//            authorize
//                .requestMatchers(AUTH_WHITELIST)
//                .permitAll()
//                .requestMatchers(AUTH_ADMIN_LIST)
//                .hasAuthority(String.valueOf(RoleType.ADMIN))
//                .requestMatchers(AUTH_ACTIVE_USER_LIST)
//                .hasAuthority(String.valueOf(RoleType.ACTIVE))
//                .anyRequest()
//                .authenticated()
//        )
//        //SET SESSION STATELESS
//        .sessionManagement()
//        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        .and()
//        .authenticationProvider(authenticationProvider)
//        .addFilterAt(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//        .addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
////        .addFilterAt(corsFilter, LogoutFilter.class)
//        .logout()
//        .logoutUrl(LOGOUT_URL) //using default Spring logout without implimentation
//        .addLogoutHandler(logoutHandler)
//        .logoutSuccessHandler(
//            (request, response, authentication) ->
//                SecurityContextHolder.clearContext()
//        )
//        .and()
//        .build();
//  }

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("*"));
    config.setAllowedMethods(List.of("*"));
    config.setAllowCredentials(true);
    config.addAllowedHeader(ALL);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

}
