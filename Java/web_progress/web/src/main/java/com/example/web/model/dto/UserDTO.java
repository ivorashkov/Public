package com.example.web.model.dto;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends BaseDTO {

  @Expose
  private Long id;

  @Expose
  private String firstName;

  @Expose
  private String lastName;

  @Expose
  private String email;

  @Expose
  private RoleDTO role;
}
