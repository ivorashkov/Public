package com.example.web.service;

import com.example.web.model.entity.UserEntity;
import java.util.List;

public interface AdminService {

  List<UserEntity> getAccountsForApproval();
}
