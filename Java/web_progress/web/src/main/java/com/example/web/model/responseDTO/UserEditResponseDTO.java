package com.example.web.model.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEditResponseDTO {
  private String email;
  private String firstName;
  private String lastName;
  private String phoneNumber;
}
