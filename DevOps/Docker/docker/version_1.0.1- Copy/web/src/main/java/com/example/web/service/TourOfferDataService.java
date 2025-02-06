package com.example.web.service;

import com.example.web.model.dto.TourOfferImagePathDTO;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.entity.TourOfferEntity;
import java.nio.file.Path;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TourOfferDataService {

  void saveFileUri(TourOfferFullDTO offerDTO, Path initPath);

  List<TourOfferImagePathDTO> findAllOfferDataPaths(Long offerId);

  List<TourOfferImagePathDTO> getOfferPaths(TourOfferFullDTO tourOfferFullDTO);
}
