package com.web_project.web_project.repository;

import com.web_project.web_project.models.entity.BaseUser;
import com.web_project.web_project.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> save(BaseUser user);

    Optional<UserEntity> findById(Long id);

}
