package com.example.web.util.impl;

import com.example.web.model.entity.BaseEntity;
import com.example.web.model.entity.UserEntity;
import com.example.web.model.enums.RoleType;
import com.example.web.util.ValidatorUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class ValidatorUtilImpl implements ValidatorUtil {

  private final Validator validator;
  private final ModelMapper mapper;

  public ValidatorUtilImpl(ModelMapper mapper) {
    this.mapper = mapper;
    this.validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();
  }

  /**
   * Works
   **/
  @Override
  public <E> boolean isValid(E entity) {
    return this.validator.validate(entity).isEmpty();
  }

  /**
   * Works
   **/
  @Override
  public <E extends UserEntity> boolean isAdmin(E entity) {
    return entity.getRole().getRoleName().equals(RoleType.admin);
  }

  /**
   * Works
   **/
  @Override
  public <E extends UserEntity> boolean isActive(E entity) {
    return entity.isActive();
  }


  /**
   * Works
   **/
  @Override
  public <E> ResponseEntity<E> responseEntity(E entity) {

    if (entity == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(entity);
    }
  }

  /**
   * Works
   **/
  @Override
  public <T, D> Page<D> mapEntityPageIntoDtoPage(Page<T> entities, Class<D> dtoClass) {
    return entities.map(objectEntity -> this.mapper.map(objectEntity, dtoClass));
  }

  /**
   * Works
   **/
  @Override
  public <E> E getCriteriaParam(E country, E city) {
    if (country != null) {
      return country;
    } else if (city != null) {
      return city;
    }
    return null;
  }

  /**
   * returns Entity if it's not deleted, else it returns null
   */
  @Override
  public <E extends BaseEntity> E isDeleted(E entity) {
    return entity.isDeleted() ? null : entity;
  }

  /**
   * Expects List<Optional<Elements> and converts it into List<NonOptional>
   */
  @Override
  public <E> List<E> getListFromOptionalList(List<Optional<E>> entities) {
    return entities
        .stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  /**
   * Return DTO List From Entity List
   */
  @Override
  public <E, D> List<D> getDTOList(List<E> entities, Class<D> dtoClass) {
    return entities.stream()
        .map(objectEntity -> this.mapper.map(objectEntity, dtoClass))
        .collect(Collectors.toList());
  }

  /**
   * maps Entity to DTO
   */
  @Override
  public <E, D> D getDTOFromEntity(E entity, Class<D> dtoClass) {
    return this.mapper.map(entity, dtoClass);
  }

  /**
   *maps DTO to Entity
   */
  @Override
  public <D, E> E getEntityFromDTO(D dto, Class<E> entityClass) {
    return this.mapper.map(dto, entityClass);
  }


}
