package com.example.web.model.dto;

import com.example.web.model.responseDTO.UserSimpleIdResponseDTO;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeDTO extends BaseDTO{

  @Expose
  private Long id;

  @Expose
  private String address;

  @Expose
  private String city;

  @Expose
  private String country;

  @Expose
  private String phone;

  @Expose
  private UserSimpleIdResponseDTO user;
}
