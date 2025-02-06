package com.example.web.repository;

import com.example.web.model.entity.AccountInfoEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfoEntity, Long> {

  List<Optional<AccountInfoEntity>> findAllByUserId(Long userId);

  Optional<AccountInfoEntity> findByDocumentLocation(String absolutePath);
}
