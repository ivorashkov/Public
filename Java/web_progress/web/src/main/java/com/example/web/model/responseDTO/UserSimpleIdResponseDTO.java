package com.example.web.model.responseDTO;

import com.example.web.model.dto.BaseDTO;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleIdResponseDTO extends BaseDTO {

  @Expose
  private Long id;

  @Expose
  private boolean isActive;

}
