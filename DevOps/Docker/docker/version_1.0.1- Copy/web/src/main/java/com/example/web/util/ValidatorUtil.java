package com.example.web.util;

import com.example.web.model.entity.BaseEntity;
import com.example.web.model.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ValidatorUtil {

  <E> boolean isValid(E entity);

  <E extends UserEntity> boolean isAdmin(E entity);

  <E extends UserEntity> boolean isActive(E entity);

  <E> ResponseEntity<E> responseEntity(E entity);

  <T, D> Page<D> mapEntityPageIntoDtoPage(Page<T> entities, Class<D> dtoClass);

  <E> E getCriteriaParam(E country, E city);

  <E extends BaseEntity> E isDeleted(E entity);

  <E> List<E> getListFromOptionalList(List<Optional<E>> entities);

  <E, D> List<D> getDTOList(List<E> entities, Class<D> dtoClass);

  <E, D> D getDTOFromEntity(E entity, Class<D> dtoClass);

  <D, E> E getEntityFromDTO(D dto, Class<E> entityClass);
}
