package com.example.web.service.impl;

import com.example.web.exception.PageWithOffersNotFoundException;
import com.example.web.exception.TourOfferNotFoundException;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.requestDto.TourOfferCreateRequestDTO;
import com.example.web.model.requestDto.TourOfferEditRequestDTO;
import com.example.web.model.responseDTO.TourOfferByIdResponseDTO;
import com.example.web.model.responseDTO.TourOfferCreateResponseDTO;
import com.example.web.model.responseDTO.TourOfferPagingResponseDTO;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.entity.TourOfferEntity;
import com.example.web.model.entity.UserEntity;
import com.example.web.model.requestDto.TourOfferDeleteRequestDTO;
import com.example.web.model.responseDTO.TourOfferShortResponseDTO;
import com.example.web.repository.TourOfferRepository;
import com.example.web.service.TourOfferFilePathService;
import com.example.web.service.TourOfferService;
import com.example.web.service.UserService;
import com.example.web.util.ValidatorUtil;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TourOfferServiceImpl implements TourOfferService {

  private final TourOfferFilePathService tourOfferFilePathService;
  private final TourOfferRepository tourOfferRepository;
  private final UserService userService;
  private final ValidatorUtil validatorUtil;

  @Override
  public Page<TourOfferPagingResponseDTO> searchAndFilterOffers(
      Integer pageNumber,
      Integer pageSize,
      String country,
      String city,
      String... sorts
  ) {
    log.info(" [INFO] Loading TourOfferServiceImpl {searchAndFilterOffers}");

    Page<TourOfferPagingResponseDTO> offers = null;
    try {
      final List<Sort.Order> orders = getOrderList(sorts);//added

      final Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(orders));

      final List<String> criteria = getCriteriaParam(country, city);

      final Page<TourOfferEntity> offerEntities;

      if (criteria == null) {
        //find all offers
        offerEntities = tourOfferRepository.findAll_TourOffers_ByDate(pageable);

        offers = this.validatorUtil.mapEntityPageIntoDtoPage(offerEntities,
            TourOfferPagingResponseDTO.class);
      } else {

        offerEntities = tourOfferRepository.findAllByCriteria(criteria.get(0), criteria.get(1),
            pageable);
        offers = this.validatorUtil.mapEntityPageIntoDtoPage(offerEntities,
            TourOfferPagingResponseDTO.class);
      }

      return offers;
    } catch (PageWithOffersNotFoundException e) {
      log.error(" [ERROR] Error while trying to extract filtered Offers Page {}", e.getMessage());

      throw e;
    }
  }

  @Override
  public TourOfferFullDTO findByIdAndUserId(Long offerId, Long userId) {
    log.info(" [INFO] TourOfferServiceImpl  { findByIdAndUserId }");

    TourOfferEntity tourEntity = null;
    try {

      tourEntity = this.tourOfferRepository.findByIdAndUserId(offerId, userId)
          .orElseThrow(TourOfferNotFoundException::new);

      var userEntity = this.validatorUtil.getEntityFromDTO
          (this.userService.findUserDTOById(userId), UserEntity.class);

      tourEntity.setUser(userEntity);

      return this.validatorUtil.getDTOFromEntity(tourEntity, TourOfferFullDTO.class);
    } catch (TourOfferNotFoundException e) {
      log.error(" [ERROR] Error TourOfferServiceImpl { findByIdAndUserId } {}", e.getMessage());

      throw e;
    }
  }

  @Override
  public TourOfferFullDTO saveOfferAndPath(Long userId, Long offerId) {
    log.info(" [INFO] TourOfferServiceImpl {saveOfferAndPath}");

    try {

      var userEntity = this.validatorUtil.getEntityFromDTO(userId, UserEntity.class);

      TourOfferEntity tourOfferEntity = this.tourOfferRepository.findById(offerId)
          .orElseThrow(TourOfferNotFoundException::new);

      tourOfferEntity.setUser(userEntity);

      return this.validatorUtil.getDTOFromEntity
          (this.tourOfferRepository.save(tourOfferEntity), TourOfferFullDTO.class);

    } catch (TourOfferNotFoundException e) {
      log.error(" [ERROR] Exception while trying to save Offer {}", e.getMessage());

      throw e;
    }
  }

  @Override
  public boolean deleteOffer(Long userId, Long offerId) {
    log.info(" [INFO] Loading TourOfferServiceImpl { deleteOffer }");

    try {
      TourOfferEntity offer =
          this.tourOfferRepository.findByIdAndUserId(offerId, userId)
              .orElseThrow(TourOfferNotFoundException::new);

      TourOfferDeleteRequestDTO dto = this.validatorUtil.getDTOFromEntity(offer,
          TourOfferDeleteRequestDTO.class);

      if (this.tourOfferFilePathService.deleteOfferFilePaths(dto)) {
        offer.setDeleted(true);
        return this.tourOfferRepository.save(offer).isPresent();
      }

    } catch (TourOfferNotFoundException e) {
      log.error(" [ERROR] Error while loading TourOfferServiceImpl { deleteOffer } {} ",
          e.getMessage());

      throw e;
    }

    return false;
  }

  @Override
  public boolean deleteOfferFilePaths(TourOfferFullDTO offerFullDTO) {
    log.info("[INFO] Loading TourOfferServiceImpl {deleteOfferFilePaths}");
    try {
      this.tourOfferFilePathService.deleteOfferFilePaths
          (this.validatorUtil.getDTOFromEntity(offerFullDTO, TourOfferDeleteRequestDTO.class));

      return true;
    } catch (Exception e) {
      log.error("[ERROR] while loading TourOfferServiceImpl {deleteOfferFilePaths}");
    }
    return false;
  }

  @Override
  public TourOfferFullDTO setNewProperties(TourOfferEditRequestDTO editDTO,
      TourOfferFullDTO fullDTO) {
    fullDTO.setDiscount(editDTO.getDiscount());
    fullDTO.setTitle(editDTO.getTitle());
    fullDTO.setCountry(editDTO.getCountry());
    fullDTO.setCity(editDTO.getCity());
    fullDTO.setDuration(editDTO.getDuration());
    fullDTO.setStars(editDTO.getStars());
    fullDTO.setPrice(editDTO.getPrice());
    fullDTO.setDescription(editDTO.getDescription());
    fullDTO.setDiscount(editDTO.getDiscount());
    fullDTO.setTransportType(editDTO.getTransportType());
    return fullDTO;
  }

  @Override
  public TourOfferByIdResponseDTO findByOfferId(Long offerId) {

    return this.validatorUtil.getDTOFromEntity(
        this.tourOfferRepository.findById(offerId).orElseThrow(TourOfferNotFoundException::new),
        TourOfferByIdResponseDTO.class
    );
  }


  @Override
  public List<String> findAllCountries() {

    return this.validatorUtil.getListFromOptionalList(
        this.tourOfferRepository.findDistinctByCountry());
  }

  @Override
  public List<String> findAllCitiesByCountryName(String countryName) {

    return this.validatorUtil.getListFromOptionalList(
        this.tourOfferRepository.findDistinctCitiesByCountryName(countryName));
  }

  @Override
  public List<TourOfferShortResponseDTO> findAllByUserId(Long userId) {

    return this.validatorUtil.getDTOList(
        this.validatorUtil.getListFromOptionalList(
            this.tourOfferRepository.findAllByUserId(userId)),
        TourOfferShortResponseDTO.class
    );
  }

  @Override
  public TourOfferCreateResponseDTO createOffer(Long userId,
      TourOfferCreateRequestDTO createRequestDTO) {
    log.info("[ INFO ] Loading TourOfferServiceImpl { createOffer }");
    UserDTO userDTO = this.userService.findUserDTOById(userId);
    TourOfferEntity tourEntity = this.validatorUtil.getEntityFromDTO(createRequestDTO,
        TourOfferEntity.class);

    tourEntity.setUser(this.validatorUtil.getEntityFromDTO(userDTO, UserEntity.class));
    tourEntity.setDate(LocalDateTime.now());
    return this.validatorUtil.getDTOFromEntity(this.tourOfferRepository.save(tourEntity),
        TourOfferCreateResponseDTO.class);
  }

  private List<Sort.Order> getOrderList(String[] sort) {
    List<Sort.Order> orders = new ArrayList<>();

    for (String s : sort) {
      String[] columns = s.split(";");
      String sortCriteria = columns[0];
      String directionCriteria = columns[1];

      Direction directionOfSort = getDirectionOfSort(directionCriteria);
      orders.add(new Sort.Order(directionOfSort, sortCriteria));

    }
    return orders;
  }

  private Sort.Direction getDirectionOfSort(String directionCriteria) {

    if ("ASC".equalsIgnoreCase(directionCriteria)) {
      return Sort.Direction.ASC;
    } else if ("DESC".equalsIgnoreCase(directionCriteria)) {
      return Sort.Direction.DESC;
    }

    return null;
  }

  private List<String> getCriteriaParam(String country, String city) {
    return this.validatorUtil.getCriteriaParam(country, city);
  }
}
