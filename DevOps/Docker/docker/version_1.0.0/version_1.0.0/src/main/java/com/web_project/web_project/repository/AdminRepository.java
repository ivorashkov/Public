package com.web_project.web_project.repository;

import com.web_project.web_project.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<UserEntity,Long> {
}
