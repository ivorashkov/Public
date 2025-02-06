package com.example.web.service;

import com.example.web.model.dto.UserDTO;
import com.example.web.model.dto.UserLoginDTO;

public interface UserService {

  UserDTO findUserDTOById(Long id);

  boolean create(UserDTO userDTO);

  String deleteUser(UserDTO userDTO);

  String updateUser(UserDTO userDTO);

  boolean login(UserLoginDTO userLoginDTO);

}
