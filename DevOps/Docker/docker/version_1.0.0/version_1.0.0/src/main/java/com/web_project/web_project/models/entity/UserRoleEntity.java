package com.web_project.web_project.models.entity;

import com.web_project.web_project.models.enums.UserRoles;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.OneToMany;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "roles")
public class UserRoleEntity extends BaseEntity {

    private UserRoles userRoles;

//    @OneToMany
//    private Set<UserEntity> userEntity;

    public UserRoleEntity() {
    }

    public UserRoleEntity(UserRoles userRoles, UserEntity userEntity) {
        this.userRoles = userRoles;
//        this.userEntity = Collections.singleton(userEntity);
    }

    public UserRoles getUserRoles() {
        return userRoles;
    }
}
