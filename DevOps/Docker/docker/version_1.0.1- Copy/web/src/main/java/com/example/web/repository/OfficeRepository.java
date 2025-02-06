package com.example.web.repository;

import com.example.web.model.dto.OfficeDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.entity.OfficeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<OfficeEntity, Long> {

  @Query("SELECT o FROM OfficeEntity o WHERE o.user.id = :userId ORDER BY o.city")
  List<Optional<OfficeEntity>> findAllOfficesByUserIdAsc(@Param("userId") Long userId);

  Optional<OfficeEntity> findByIdAndUserId(Long officeId, Long userId);

}
