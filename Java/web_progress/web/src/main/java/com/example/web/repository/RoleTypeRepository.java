package com.example.web.repository;

import com.example.web.model.entity.RoleEntity;
import com.example.web.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleTypeRepository extends JpaRepository<RoleEntity, Long> {

  RoleEntity findByRoleName(RoleType roleName);
}
