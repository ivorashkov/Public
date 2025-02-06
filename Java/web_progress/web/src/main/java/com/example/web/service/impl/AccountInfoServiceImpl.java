package com.example.web.service.impl;


import com.example.web.model.dto.AccountInfoDTO;
import com.example.web.model.dto.UserDTO;
import com.example.web.model.entity.AccountInfoEntity;
import com.example.web.model.entity.UserEntity;
import com.example.web.service.AccountInfoService;
import com.example.web.util.ValidatorUtil;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AccountInfoServiceImpl implements
    AccountInfoService {

  private final com.example.web.repository.AccountInfoRepository additionalAccountInfoRepository;
  private final ValidatorUtil validatorUtil;

  @Override
  public void saveFileUri(UserDTO userDTO, Path absoluteDocumentLocation) {
    log.info(" [INFO] Loading AccountInfoServiceImpl {saveFileUri} ");
    Optional<AccountInfoEntity> isAlreadyInDB =
        this.additionalAccountInfoRepository.findByDocumentLocation(absoluteDocumentLocation.toString());

    if (isAlreadyInDB.isEmpty()) {
      var userEntity = this.validatorUtil.getEntityFromDTO(userDTO, UserEntity.class);

      var additionalInfoEntity = new AccountInfoEntity(absoluteDocumentLocation.toString(),
          userEntity);

      this.additionalAccountInfoRepository.save(additionalInfoEntity);
    }
  }


  @Override
  public List<AccountInfoDTO> findAllAccountDataPaths(UserDTO user) {
    log.info(" [INFO] Loading AccountInfoServiceImpl {findAllAccountDataPaths}");
    List<AccountInfoEntity> accountInfoEntity = this.validatorUtil.getListFromOptionalList
        (
            this.additionalAccountInfoRepository.findAllByUserId(user.getId())
        );

    return this.validatorUtil.getDTOList(accountInfoEntity, AccountInfoDTO.class);
  }
}
