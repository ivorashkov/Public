package com.example.web.repository;

import com.example.web.model.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPanelRepository extends JpaRepository<UserEntity, Long> {
//  List<UserEntity> findAllByActiveFalseAndOrderById();


}
