package com.example.web.security.service.impl;

import com.example.web.exception.UserNotFoundException;
import com.example.web.model.entity.UserEntity;
import com.example.web.repository.UserRepository;
import com.example.web.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userEntity = this.userRepository.findUserEntityByEmail(email)
        .orElseThrow(UserNotFoundException::new);

    //todo to check why bad credentials appears here?????????
    return new CustomUserDetails(userEntity);
  }
}
