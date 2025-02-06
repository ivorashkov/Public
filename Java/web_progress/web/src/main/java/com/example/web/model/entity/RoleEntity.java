package com.example.web.model.entity;

import com.example.web.model.enums.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity extends BaseEntity {

  @Column(unique = true)
  @Enumerated(EnumType.STRING)
  private RoleType roleName;

  @Override
  public void setDeleted(boolean isDeleted) {
    isDeleted = false;
    super.setDeleted(isDeleted);
  }
}
