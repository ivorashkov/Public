package com.example.web.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "additional_user_info")
public class AccountInfoEntity extends BaseEntity {


  @Column(name = "document_path", nullable = false)
  private String documentLocation;

  @ManyToOne
  private UserEntity user;

}
