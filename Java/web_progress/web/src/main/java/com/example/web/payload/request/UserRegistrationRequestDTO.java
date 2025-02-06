package com.example.web.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequestDTO {

  @Email
  @NotEmpty
  @NotNull
  private String email;

  @Size(min = 1)
  private String firstName;

  @Size(min = 1)
  private String lastName;

  @Size(min = 8, max = 15)
  private String password;

  @Size(min = 8, max = 15)
  private String passwordConfirm;

  @Size(min = 10, max = 10)
  private String phoneNumber;

}
