package com.example.web.security.repository;

import com.example.web.security.token.TokenEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

  //all valid tokens for user
  List<Optional<TokenEntity>> findAllByUserIdAndExpiredIsFalseAndRevokedIsFalse(Long userId);

  //find token by token string
  Optional<TokenEntity> findByToken(String token);
}
