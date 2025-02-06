package com.example.web.controller;

import com.example.web.model.responseDTO.CityListResponseDTO;
import com.example.web.model.responseDTO.CountryListResponseDTO;
import com.example.web.model.responseDTO.TourOfferByIdResponseDTO;
import com.example.web.model.responseDTO.TourOfferPagingResponseDTO;
import com.example.web.service.TourOfferService;
import com.example.web.util.ValidatorUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

  private final TourOfferService tourOfferService;
  private final ValidatorUtil validatorUtil;

  @GetMapping("/offers")
  public ResponseEntity<Page<TourOfferPagingResponseDTO>> findAllAndSort(
      @RequestParam(name = "page", defaultValue = "0") Integer page,
      @RequestParam(name = "pageSize", defaultValue = "40") Integer size,
      @RequestParam(name = "country", required = false) String country,
      @RequestParam(name = "city", required = false) String city,
      @RequestParam(defaultValue = "date;desc") String... sort
  ) {

    return this.validatorUtil.responseEntity(
        this.tourOfferService.searchAndFilterOffers(page, size, country, city, sort));
  }

  @GetMapping("/country/list")
  public ResponseEntity<CountryListResponseDTO> getAllCountries() {

    return this.validatorUtil.responseEntity(CountryListResponseDTO.builder()
        .countries(this.tourOfferService.findAllCountries())
        .build());
  }

  @GetMapping("/offer/{id}")
  public ResponseEntity<TourOfferByIdResponseDTO> getOfferById(@PathVariable Long id) {

    return this.validatorUtil.responseEntity(this.tourOfferService.findByOfferId(id));
  }

  @GetMapping("/city/list")
  public ResponseEntity<CityListResponseDTO> getAllCitiesForCountryName(
      @RequestParam(name = "country") String countryName
  ) {

    return this.validatorUtil.responseEntity(CityListResponseDTO.builder()
        .cities(this.tourOfferService.findAllCitiesByCountryName(countryName))
        .build());
  }
}
