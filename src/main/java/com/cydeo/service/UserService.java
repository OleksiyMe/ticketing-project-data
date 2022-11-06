package com.cydeo.service;

import com.cydeo.dto.UserDTO;

import javax.transaction.Transactional;
import java.util.List;


public interface UserService {

    List<UserDTO> listAllUsers();

    UserDTO findByUserName(String username);

    void save(UserDTO user);

    @Transactional
    void deleteByUsername(String username);

    UserDTO update(UserDTO user);

    void delete(String username);

    List<UserDTO> listAllByRole(String role);
}
