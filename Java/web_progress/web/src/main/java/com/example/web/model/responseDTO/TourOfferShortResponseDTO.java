package com.example.web.model.responseDTO;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TourOfferShortResponseDTO {

  private Long id;
  private String title;
  private int duration;
  private BigDecimal price;
  private String description;
  private double discount;

}
