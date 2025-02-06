package com.example.web.service.impl;

import com.example.web.model.dto.TourOfferCreateDTO;
import com.example.web.model.dto.TourOfferPagingDTO;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.entity.TourOfferEntity;
import com.example.web.model.entity.UserEntity;
import com.example.web.repository.OfferDataRepository;
import com.example.web.repository.TourOfferRepository;
import com.example.web.service.TourOfferService;
import com.example.web.service.UserService;
import com.example.web.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TourOfferServiceImpl implements TourOfferService {

  private final TourOfferRepository tourOfferRepository;
  private final UserService userService;
  private final ValidatorUtil validatorUtil;

  @Override
  public Page<TourOfferPagingDTO> initialSearchResult(Integer pageNumber, Integer pageSize) {
    //TODO ***********************************
    //TODO ***********************************
    //TODO Адекватно ли е тук да има try-catch или има по-добър вариант
    //TODO ***********************************

    Page<TourOfferPagingDTO> offerDTOS;
    try {

    //  log.info("initialSearchResult");
      Page<TourOfferEntity> offerEntityPage = this.tourOfferRepository
          .findAll_TourOffers_ByDate(PageRequest.of(pageNumber, pageSize));

      offerDTOS = this.validatorUtil.mapEntityPageIntoDtoPage(offerEntityPage,
          TourOfferPagingDTO.class);

      return offerDTOS;

    } catch (Exception e) {
  //   log.error("Issue while trying to extract Offers initialSearchResult {}", e.getMessage());
     throw new NullPointerException();//todo custom error thrown
    }
  }



  @Override
  public Page<TourOfferPagingDTO> searchAndFilterOffers(
      Integer pageNumber,
      Integer pageSize,
      String country,
      String city,
      String... sorts
  ) {
    //TODO ***********************************
    //TODO ***********************************
    //TODO Адекватно ли е тук да има try-catch или има по-добър вариант
    //TODO ***********************************
    Page<TourOfferPagingDTO> offers = null;
    try {
      final List<Sort.Order> orders = getOrderList(sorts);//added

      final Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(orders));

      final String criteria = getCriteriaParam(country, city);

      final Page<TourOfferEntity> offerEntities;

      if (criteria == null) {
        //works
        offerEntities = tourOfferRepository.findAll_TourOffers_ByDate(pageable);
        offers = this.validatorUtil.mapEntityPageIntoDtoPage(offerEntities,
            TourOfferPagingDTO.class);
      } else {
        offerEntities = tourOfferRepository.findAllByCriteria(criteria, pageable);
        offers = this.validatorUtil.mapEntityPageIntoDtoPage(offerEntities,
            TourOfferPagingDTO.class);
      }
      return offers;

    } catch (Exception e) {
      throw new NullPointerException(
          "Issue while trying to extract Offers searchAndFilterOffers" + e.getMessage());
    }
  }

  @Override
  public TourOfferFullDTO getOfferWithPathsAndUsersDTOs(Long offerId, Long userId) {

    var tourEntity = this.tourOfferRepository.findByIdAndUserId(offerId, userId);

    var userEntity = this.validatorUtil.getEntityFromDTO(this.userService.findUserDTOById(userId),
        UserEntity.class);

    //TODO ***********************************
    //TODO tourEntity е Optional каква е най-добрата опция да работим с него
    //TODO *************************************
    //TODO тук ли трябва да направим проверка ако офертата е изтрита - isDeleted(true)
    //TODO ***********************************
    //TODO CUSTOM ERROR + Controller advice
    //Logging
    tourEntity.ifPresent(e -> e.setUser(userEntity));

    return this.validatorUtil.getDTOFromEntity(tourEntity.get(), TourOfferFullDTO.class);
  }

  @Override
  public TourOfferFullDTO saveOfferAndPath(TourOfferCreateDTO importedOfferDTO) {

    var userEntity = this.validatorUtil.getEntityFromDTO
        (this.userService.findUserDTOById(importedOfferDTO.getUser().getId()), UserEntity.class);

    var tourOfferEntity = this.validatorUtil.getEntityFromDTO
        (importedOfferDTO, TourOfferEntity.class);
    tourOfferEntity.setUser(userEntity);

    return this.validatorUtil.getDTOFromEntity
        (this.tourOfferRepository.save(tourOfferEntity), TourOfferFullDTO.class);

  }

  @Override
  public void deleteOffer(Long userId, Long offerId) {
    //TODO ***********************************
    //TODO ***********************************
    //TODO Добре ли е структориран този метод и по-скоро обработката на Опшанъла
    //TODO ***********************************

    var offerEntity =
        this.tourOfferRepository.findByIdAndUserId(offerId, userId);

    offerEntity.ifPresent(entity -> {
      entity.setDeleted(true);
      this.tourOfferRepository.save(entity);
    });
  }

  @Override
  public TourOfferFullDTO findById(Long id, UserDTO userDTO) {
    //TODO ***********************************
    //TODO ***********************************
    //TODO тук сетваме User заради fetch.LAZY към ентитито преди да го обърнем в ДТО
    //TODO има ли по-добър вариант или е направено ОК.
    //TODO ***********************************
    var userEntity = this.validatorUtil.getEntityFromDTO(userDTO, UserEntity.class);

    //TODO сложил съм nullPointerException да се хвърля при липса на такава оферта
    //todo възможно ли е да се избегне Ексепшъна и да се игнорира просто или да се върне някакъв
    //todo код с който да се потвърди че няма такъв обект, а ако съществува само тогава да се обработи и прати
    var tourOfferEntity = this.tourOfferRepository.findByIdAndDeletedEqualsFalse(id)
        .orElseThrow(NullPointerException::new);

    tourOfferEntity.setUser(userEntity);

    //todo тук ще има ексепшън най-вероятно ако офертата излезе празна, трябва ли да има
    //todo допълнителна проверка и примерно да се сетне throw exception?
    return this.validatorUtil.getDTOFromEntity(tourOfferEntity, TourOfferFullDTO.class);
  }

  private List<Sort.Order> getOrderList(String[] sort) {
    List<Sort.Order> orders = new ArrayList<>();

    for (String s : sort) {
      String[] columns = s.split(";");
      String sortCriteria = columns[0];
      String directionCriteria = columns[1];

      orders.add(new Sort.Order(getDirectionOfSort(directionCriteria), sortCriteria));
    }
    return orders;
  }

  private Sort.Direction getDirectionOfSort(String directionCriteria) {
    //Order order1 = new Order(Sort.Direction.DESC, "published");
    if ("ASC".equalsIgnoreCase(directionCriteria)) {
      return Sort.Direction.ASC;
    } else if ("DESC".equalsIgnoreCase(directionCriteria)) {
      return Sort.Direction.DESC;
    }
    return null;
  }

  private String getCriteriaParam(String country, String city) {
    return this.validatorUtil.getCriteriaParam(country, city);
  }

}
