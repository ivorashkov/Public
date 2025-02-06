package com.example.web.repository;

import com.example.web.model.entity.TourOfferImagePathEntity;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferDataRepository extends JpaRepository<TourOfferImagePathEntity, Long> {


  List<Optional<TourOfferImagePathEntity>> findAllByOfferId(Long id);

  Optional<TourOfferImagePathEntity> findByDocumentLocation(String path);
}
