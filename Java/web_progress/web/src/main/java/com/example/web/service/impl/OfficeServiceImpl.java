package com.example.web.service.impl;

import com.example.web.model.dto.OfficeDTO;
import com.example.web.model.entity.OfficeEntity;
import com.example.web.repository.OfficeRepository;
import com.example.web.service.OfficeService;
import com.example.web.util.ValidatorUtil;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OfficeServiceImpl implements OfficeService {
  private final OfficeRepository officeRepository;
  private final ValidatorUtil validatorUtil;

  @Override
  public List<OfficeDTO> addOffice(OfficeDTO officeDTO) {
    log.info(" [INFO] Loading OfficeServiceImpl { addOffice }");
    return saveOfficeAndReturnAllOffices(officeDTO);
  }

  @Override
  public boolean deleteOffice(OfficeDTO officeDTO) {
    log.info(" [INFO] Loading OfficeServiceImpl { deleteOffice }");
    Optional<OfficeEntity> officeEntity = this.officeRepository.findByIdAndUserId
        (
            officeDTO.getId(),
            officeDTO.getUser().getId()
        );

    if (officeEntity.isPresent()) {
      officeEntity.get().setDeleted(true);
      this.officeRepository.save(officeEntity.get());

      return true;
    }else{

      return false;
    }
  }

  @Override
  public List<OfficeDTO> editOffice(OfficeDTO officeDTO) {
    log.info(" [INFO] Loading OfficeServiceImpl {editOffice}");
    return saveOfficeAndReturnAllOffices(officeDTO);
  }

  private List<OfficeDTO> saveOfficeAndReturnAllOffices(OfficeDTO officeDTO) {
    log.info(" [INFO] Loading OfficeServiceImpl {saveOfficeAndReturnAllOffices}");
    var officeEntity = this.validatorUtil.getEntityFromDTO(officeDTO, OfficeEntity.class);
    this.officeRepository.save(officeEntity);

    List<OfficeEntity> officeEntityList =
        this.validatorUtil.getListFromOptionalList
            (this.officeRepository.findAllOfficesByUserIdAsc(officeDTO.getUser().getId()));

    return this.validatorUtil.getDTOList(officeEntityList, OfficeDTO.class);
  }
}
