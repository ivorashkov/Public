package com.example.web.model.dto;

import com.example.web.model.interfaces.DeletableObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseDTO implements DeletableObject {

  @JsonIgnore
  private boolean isDeleted;

  @Override
  public boolean isDeleted() {
    return isDeleted;
  }
}
