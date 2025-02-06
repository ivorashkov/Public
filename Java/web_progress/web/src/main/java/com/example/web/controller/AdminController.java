package com.example.web.controller;

import com.example.web.model.entity.UserEntity;
import com.example.web.repository.UserRepository;
import com.example.web.service.AdminService;
import com.example.web.util.ValidatorUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

  private final AdminService adminService;
  private final UserRepository userRepository;
  private final ValidatorUtil validatorUtil;

  @GetMapping("/request")
  public ResponseEntity<List<UserEntity>> getAccountsForApproval(){
    List<UserEntity> accountsForApproval = this.adminService.getAccountsForApproval();
//    return this.validatorUtil.listResponseEntity()
    return this.validatorUtil.responseEntityList(accountsForApproval);
  }

}
