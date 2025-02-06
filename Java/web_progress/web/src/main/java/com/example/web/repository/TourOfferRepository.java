package com.example.web.repository;

import com.example.web.model.entity.TourOfferEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourOfferRepository extends PagingAndSortingRepository<TourOfferEntity, Long> {

  @Query("SELECT t FROM TourOfferEntity t ORDER BY t.date")
  Page<TourOfferEntity> findAll_TourOffers_ByDate(Pageable pageable);

//  @Query("SELECT t FROM TourOfferEntity t " +
//      " WHERE t.title LIKE CONCAT('%',:query, '%')" +
//      " OR t.description LIKE CONCAT('%', :query, '%')" +
//      " OR t.city LIKE CONCAT('%', :query, '%')" +
//      " OR t.country LIKE CONCAT('%', :query, '%')")

  @Query("SELECT t FROM TourOfferEntity t " +
      " WHERE t.country LIKE CONCAT('%',:country, '%')" +
      " AND t.city LIKE CONCAT('%', :city, '%')")
  Page<TourOfferEntity> findAllByCriteria(
      @Param("country") String country, @Param("city") String city, Pageable pageable);

  @Query("SELECT t FROM TourOfferEntity t"
      + " WHERE t.id=:offerId AND t.user.id=:userId AND t.isDeleted=false")
  Optional<TourOfferEntity> findByIdAndUserId
      (@Param("offerId") Long offerId, @Param("userId") Long userId);

  Optional<TourOfferEntity> save(TourOfferEntity tourOfferEntity);

  Optional<TourOfferEntity> findById(Long offerId);

  @Query("SELECT DISTINCT t.country FROM TourOfferEntity t")
  List<Optional<String>> findDistinctByCountry();

  @Query("SELECT DISTINCT t.city "
      + " FROM TourOfferEntity t "
      + " WHERE t.country LIKE CONCAT('%', :name, '%')")
  List<Optional<String>> findDistinctCitiesByCountryName(@Param("name") String name);

  List<Optional<TourOfferEntity>> findAllByUserId(Long userId);

}
