package com.example.web.service.impl;

import com.example.web.model.entity.AccountInfoEntity;
import com.example.web.model.entity.UserEntity;
import com.example.web.repository.AccountInfoRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.AdminService;
import com.example.web.util.ValidatorUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final UserRepository userRepository;
  private final AccountInfoRepository accountInfoRepository;
  private final ValidatorUtil validatorUtil;

  @Override
  public List<UserEntity> getAccountsForApproval() {
    List<UserEntity> usersForApproval = this.validatorUtil.getListFromOptionalList(
        this.userRepository.findAllInactiveUsers());

//    usersForApproval.forEach(u -> {
//      List<AccountInfoEntity> AccountDataProvided = this.validatorUtil.getListFromOptionalList(
//          this.accountInfoRepository.findAllByUserId(
//              u.getId()));
//    });
//    Could not write JSON: failed to lazily initialize a collection of role: com.example.web.model.entity.UserEntity.tokens: could not initialize proxy - no Session]
    return usersForApproval;
  }
}
