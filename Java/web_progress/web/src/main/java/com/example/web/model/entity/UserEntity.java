package com.example.web.model.entity;

import com.example.web.security.token.TokenEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.List;
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
@Table(name = "users")
public class UserEntity extends BaseEntity {

  @Column(nullable = false)
  private String password;

  @Email
  @Column(nullable = false, unique = true, name = "email_address")
  private String email;

  @Column(nullable = false, name = "first_name")
  private String firstName;

  @Column(nullable = false, name = "last_name")
  private String lastName;

  @Column(nullable = false, unique = true, name = "phone_number")
  private String phoneNumber;

  @OneToOne
  private RoleEntity role;

  @Column
  private int approvedBy;

  @Column(columnDefinition = "boolean default false")
  private boolean hasStar;

  @OneToMany(mappedBy = "user")
  private List<TokenEntity> tokens;

}
