package com.example.web.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {

  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String password;
  private String passwordConfirm;

}
