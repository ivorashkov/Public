package com.example.web.repository;

import com.example.web.model.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPanelRepository extends JpaRepository<UserEntity, Long> {

  @Query("SELECT u FROM UserEntity u WHERE u.isDeleted = false AND u.role.roleName = 'inactive'")
  List<UserEntity> findAllInactiveUsers();


  @Query("SELECT u FROM UserEntity u WHERE u.isDeleted = false AND u.role.roleName ='inactive' ")
  List<UserEntity> findAllActiveUsers();

  @Query("SELECT u FROM UserEntity u WHERE u.isDeleted = true")
  List<UserEntity> findAllDeletedUsers();

}
