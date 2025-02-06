package com.example.web.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "offices")
public class OfficeEntity extends BaseEntity {

  @Column(nullable = false)
  private String country;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false, unique = true)
  private String phone;

  @ManyToOne(fetch = FetchType.LAZY)
  private UserEntity user;

}
