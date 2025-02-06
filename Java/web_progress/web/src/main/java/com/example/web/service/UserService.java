package com.example.web.service;

import com.example.web.model.dto.UserDTO;
import com.example.web.model.requestDto.UserEditRequestDTO;
import com.example.web.model.responseDTO.UserEditResponseDTO;

public interface UserService {

  UserDTO findUserDTOById(Long id);

  boolean create(UserDTO userDTO);

  boolean deleteUser(UserDTO userDTO);

  boolean updateUser(UserDTO userDTO);

  UserEditResponseDTO findUserDTOByEmail(UserEditRequestDTO userEditRequestDTO);

  UserEditResponseDTO save(UserEditResponseDTO userEditRequestDTO);
}
