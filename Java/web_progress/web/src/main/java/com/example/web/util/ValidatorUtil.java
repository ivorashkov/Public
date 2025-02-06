package com.example.web.util;

import com.example.web.model.entity.BaseEntity;
import com.example.web.model.entity.TourOfferFilePathEntity;
import com.example.web.model.entity.UserEntity;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface ValidatorUtil {

  <E> boolean isValid(E entity);

  <E extends UserEntity> boolean isAdmin(E entity);

  <E> ResponseEntity<E> responseEntity(E entity);

  <E> ResponseEntity<E> responseEntityBoolean(boolean b);

  <E> ResponseEntity<List<E>> responseEntityList(List<E> entities);

  <T, D> Page<D> mapEntityPageIntoDtoPage(Page<T> entities, Class<D> dtoClass);

  List<String> getCriteriaParam(String country, String city);

  <E extends BaseEntity> E isDeleted(E entity);

  <E> List<E> getListFromOptionalList(List<Optional<E>> entities);

  <E, D> List<D> getDTOList(List<E> entities, Class<D> dtoClass);

  <E, D> D getDTOFromEntity(E entity, Class<D> dtoClass);

  <D, E> E getEntityFromDTO(D dto, Class<E> entityClass);

  <E> String getLogInfo(Class<E> claz, Method method);

//  <E extends TourOfferFilePathEntity> List<String> readFileToString(List<E> list);
}
