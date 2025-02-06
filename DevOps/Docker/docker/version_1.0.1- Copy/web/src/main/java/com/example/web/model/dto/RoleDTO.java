package com.example.web.model.dto;

import com.example.web.model.enums.RoleType;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

  @Expose
  private Long id;

  @Expose
  private RoleType roleName;

}
