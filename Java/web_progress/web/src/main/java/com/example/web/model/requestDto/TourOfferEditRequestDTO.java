package com.example.web.model.requestDto;

import com.example.web.model.dto.BaseDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.enums.TransportType;
import com.google.gson.annotations.Expose;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TourOfferEditRequestDTO extends BaseDTO {

  @Expose
  private Long id;

  @Expose
  private UserDTO user;

  @Expose
  private String title;

  @Expose
  private String country;

  @Expose
  private String city;

  @Expose
  private int duration;

  @Expose
  private double stars;

  @Expose
  private BigDecimal price;

  @Expose
  private String description;

  @Expose
  private double discount;

  @Expose
  private TransportType transportType;

}
