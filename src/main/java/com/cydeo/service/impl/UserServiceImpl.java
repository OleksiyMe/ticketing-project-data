package com.cydeo.service.impl;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> listAllUsers() {

        List<User> userList = userRepository.findAll(Sort.by("firstName"));


        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        User user = userRepository.findByUserName(username);

        return userMapper.convertToDto(user);
    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

    @Override
    public void deleteByUsername(String username) {

        //  userRepository.deleteById(userRepository.findByUserName(username).getId());
        userRepository.deleteByUserName(username);


    }

    @Override
    public UserDTO update(UserDTO userDto) {

        //Find teh current user

        User user = userRepository.findByUserName(userDto.getUserName()); //has id
        //map updated user dto to entity
        User convertedUser = userMapper.convertToEntity(userDto);  //no ID
        //set id to the converted object
        convertedUser.setId(user.getId());

        userRepository.save(convertedUser);
        return findByUserName(userDto.getUserName());

        /*User user= userMapper.convertToEntity(userDto);

        userRepository.save(user);

        return userDto;*/
    }

    @Override
    public void delete(String username) {
        //go to DB and get user with username
        //change the isDeleted field to true, save in the db

        User user = userRepository.findByUserName(username);
        user.setIsDeleted(true);
        userRepository.save(user);

    }

    @Override
    public List<UserDTO> listAllByRole(String role) {

        List<User> users = userRepository.findByRoleDescriptionIgnoreCase(role);

        return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }
}
