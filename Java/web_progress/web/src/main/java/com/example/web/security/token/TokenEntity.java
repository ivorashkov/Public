package com.example.web.security.token;

import com.example.web.model.entity.BaseEntity;
import com.example.web.model.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "tokens")
public class TokenEntity extends BaseEntity {

  private String token;

  @Enumerated(EnumType.STRING)
  private TokenType tokenType;

  private boolean expired;

  private boolean revoked;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

}
