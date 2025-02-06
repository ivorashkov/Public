package com.example.web.util.impl;

import com.example.web.constant.LoggingMessageConstants;
import com.example.web.model.entity.BaseEntity;
import com.example.web.model.entity.UserEntity;
import com.example.web.model.enums.RoleType;
import com.example.web.util.ValidatorUtil;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ValidatorUtilImpl implements ValidatorUtil {

  private final Validator validator;
  private final ModelMapper mapper;

  public ValidatorUtilImpl(ModelMapper mapper) {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      this.validator = factory.getValidator();
    }
    this.mapper = mapper;
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
    return entity.getRole().getRoleName().equals(RoleType.ADMIN);
  }

  /**
   * Works
   **/
  @Override
  public <E> ResponseEntity<E> responseEntity(E entity) {

    if (entity != null) {
      return ResponseEntity.ok(entity);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public <E> ResponseEntity<E> responseEntityBoolean(boolean b) {
    if (b) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @Override
  public <E> ResponseEntity<List<E>> responseEntityList(List<E> entities) {
    if (!entities.isEmpty()) {
      return ResponseEntity.ok(entities);
    } else {
      return ResponseEntity.badRequest().build();
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
  public List<String> getCriteriaParam(String country, String city) {
    if (country != null && city != null) {
      return List.of(country, city);
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
   * maps DTO to Entity
   */
  @Override
  public <D, E> E getEntityFromDTO(D dto, Class<E> entityClass) {
    return this.mapper.map(dto, entityClass);
  }

  @Override
  public <E> String getLogInfo(Class<E> claz, Method method) {
    return String.format(LoggingMessageConstants.LOG_INFO, claz.getSimpleName(),
        method);
  }

//  @Override
//  public <E extends TourOfferFilePathEntity> List<String> readFileToString(List<E> list) {
//    List<String> newPaths = new ArrayList<>();
//    list.forEach(TourOfferFilePathEntity -> {
//      //path
//      File file = new File(TourOfferFilePathEntity.getDocumentLocation());
//      try {
//        byte[] bytes = Files.readAllBytes(file.toPath());
//        String string = Arrays.toString(bytes);
//        newPaths.add(string);
//
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//      System.out.println();
//      //read
//      //transfer to string
//    });
//    return newPaths;
//  }

//  @Override
//  public <E> String getErrrorLog(Class<E> claz) {
//    return String.format(LoggingMessageConstants.LOG_INFO, claz.getSimpleName(),
//        claz.getEnclosingMethod().getName());
//  }
}
