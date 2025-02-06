package com.example.web.service.impl;

import com.example.web.model.dto.TourOfferImagePathDTO;
import com.example.web.model.dto.TourOfferFullDTO;
import com.example.web.model.entity.TourOfferImagePathEntity;
import com.example.web.model.entity.TourOfferEntity;
import com.example.web.repository.OfferDataRepository;

import com.example.web.service.TourOfferDataService;
import com.example.web.util.ValidatorUtil;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TourOfferImagePathServiceImpl implements TourOfferDataService {

  private final OfferDataRepository offerDataRepository;
  private final ValidatorUtil validatorUtil;
  private final ModelMapper mapper;

  @Override
  public void saveFileUri(TourOfferFullDTO offerDTO, Path absoluteDocumentLocation) {
    Optional<TourOfferImagePathEntity> isAlreadyInDB =
        this.offerDataRepository.findByDocumentLocation(absoluteDocumentLocation.toString());

    if (isAlreadyInDB.isEmpty()){
      var tourOfferEntity = this.mapper.map(offerDTO,
          TourOfferEntity.class);

      var dataPathEntity = new TourOfferImagePathEntity(absoluteDocumentLocation.toString(), tourOfferEntity);

      this.offerDataRepository.save(dataPathEntity);
    }
  }

  @Override
  public List<TourOfferImagePathDTO> findAllOfferDataPaths(Long offerId) {
    List<TourOfferImagePathEntity> offerDataPathEntities = this.validatorUtil.getListFromOptionalList
        (
            this.offerDataRepository.findAllByOfferId(offerId)
        );

    return this.validatorUtil.getDTOList(offerDataPathEntities, TourOfferImagePathDTO.class);
  }

  @Override
  public List<TourOfferImagePathDTO> getOfferPaths(TourOfferFullDTO tourOfferFullDTO) {
    List<TourOfferImagePathEntity> paths = this.validatorUtil.getListFromOptionalList
        (
            this.offerDataRepository.findAllByOfferId(tourOfferFullDTO.getId())
        );

    return this.validatorUtil.getDTOList(paths, TourOfferImagePathDTO.class);
  }
}
