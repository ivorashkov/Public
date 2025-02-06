package com.example.web.payload.response;

import com.example.web.model.dto.RoleDTO;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponseDTO {
  @Expose
  private Long id;

  //  @JsonIgnore
  @Expose
  private boolean isActive;

  @Expose
  private String firstName;

  @Expose
  private String lastName;

  @Expose
  private String email;

  @Expose
  private RoleDTO role;

}
