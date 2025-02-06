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
public class TourOfferFilePathDTO extends BaseDTO {

  @Expose
  private Long id;

  @Expose
  private String documentLocation;

}
