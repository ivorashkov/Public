package com.example.web.service;

import com.example.web.model.requestDto.TourOfferCreateRequestDTO;
import com.example.web.model.requestDto.TourOfferEditRequestDTO;
import com.example.web.model.responseDTO.TourOfferByIdResponseDTO;
import com.example.web.model.responseDTO.TourOfferCreateResponseDTO;
import com.example.web.model.responseDTO.TourOfferPagingResponseDTO;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.responseDTO.TourOfferShortResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TourOfferService {

  Page<TourOfferPagingResponseDTO> searchAndFilterOffers(
      Integer pageNumber,
      Integer pageSize,
      String country,
      String city,
      String... sorts
  );

  TourOfferFullDTO findByIdAndUserId(Long offerId, Long userId);

  TourOfferFullDTO saveOfferAndPath(Long userId, Long offerId);

  boolean deleteOffer(Long userId, Long offerId);

  boolean deleteOfferFilePaths(TourOfferFullDTO offerFullDTO);

  TourOfferFullDTO setNewProperties(TourOfferEditRequestDTO editDTO, TourOfferFullDTO fullDTO);

  TourOfferByIdResponseDTO findByOfferId(Long offerId);

  List<String> findAllCountries();

  List<String> findAllCitiesByCountryName(String countryName);

  List<TourOfferShortResponseDTO> findAllByUserId(Long userId);

  TourOfferCreateResponseDTO createOffer(Long userId, TourOfferCreateRequestDTO createRequestDTO);
}
