package com.example.web.repository;

import com.example.web.model.entity.TourOfferFilePathEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferDataRepository extends JpaRepository<TourOfferFilePathEntity, Long> {


  List<Optional<TourOfferFilePathEntity>> findAllByOfferId(Long id);

  Optional<TourOfferFilePathEntity> findByDocumentLocation(String path);
}
