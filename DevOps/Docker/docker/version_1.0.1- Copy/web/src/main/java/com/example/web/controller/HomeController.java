package com.example.web.controller;

import com.example.web.model.dto.TourOfferPagingDTO;
import com.example.web.service.TourOfferService;
import com.example.web.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("")
public class HomeController {

  private final TourOfferService tourOfferService;
  private final ValidatorUtil validatorUtil;

  @GetMapping("/search")
  public ResponseEntity<Page<TourOfferPagingDTO>> response(
      @RequestParam(name = "page", defaultValue = "0") Integer page,
      @RequestParam(name = "pageSize", defaultValue = "30") Integer size
  ) {

    /** http://localhost:8091/search?criteria=France **/
    return this.validatorUtil.responseEntity(tourOfferService.initialSearchResult(page, size));
    //should redirect to TourOfferController "/tourOffers" for more filters etc.
  }


  @GetMapping("/offers")
  public ResponseEntity<Page<TourOfferPagingDTO>> findAllAndSort(
      @RequestParam(name = "page", defaultValue = "0") Integer page,
      @RequestParam(name = "pageSize", defaultValue = "30") Integer size,
      @RequestParam(name = "country", required = false) String country,
      @RequestParam(name = "city", required = false) String city,
      @RequestParam(defaultValue = "date;desc") String... sort

  ) {

    /** http://localhost:8091/offer?sort=column1,direction1&sort=column2,direction2 provides
     * with 2 columns column1,direction1 */

    return this.validatorUtil.responseEntity(
        this.tourOfferService.searchAndFilterOffers(page, size, country, city, sort));
  }

}
