package com.example.web.model.requestDto;

import com.example.web.model.dto.BaseDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.enums.TransportType;
import com.google.gson.annotations.Expose;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TourOfferCreateRequestDTO extends BaseDTO {

  @Expose
  @NotNull
  @NotEmpty
  private String title;

  @Expose
  @NotNull
  @NotEmpty
  private String country;

  @Expose
  @NotNull
  @NotEmpty
  private String city;

  @Expose
  @NotNull
  @NotEmpty
  private int duration;

  @Expose
  @NotNull
  @NotEmpty
  private int stars;

  @Expose
  @NotNull
  @NotEmpty
  private BigDecimal price;

  @Expose
  @NotNull
  @NotEmpty
  private String description;

  @Expose
  @NotNull
  @NotEmpty
  private double discount;

  @Expose
  @NotNull
  @NotEmpty
  private TransportType transportType;

}
