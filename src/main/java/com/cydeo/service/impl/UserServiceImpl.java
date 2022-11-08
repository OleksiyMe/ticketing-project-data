package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ProjectService projectService;

    private final TaskService taskService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           @Lazy ProjectService projectService, @Lazy TaskService taskService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @Override
    public List<UserDTO> listAllUsers() {

        List<User> userList = userRepository.findAllByIsDeletedOrderByFirstNameDesc(
                false);


        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        User user = userRepository.findByUserNameAndIsDeleted(username, false);

        return userMapper.convertToDto(user);
    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

    @Override
    public void deleteByUsername(String username) {    //hard delete, not used

        //  userRepository.deleteById(userRepository.findByUserName(username).getId());
        userRepository.deleteByUserName(username);


    }

    @Override
    public UserDTO update(UserDTO userDto) {

        //Find teh current user

        User user = userRepository.findByUserNameAndIsDeleted(userDto.getUserName(),false); //has id
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

        User user = userRepository.findByUserNameAndIsDeleted(username, false);

        if (checkIfUserCanBeDeleted(user)){

            user.setIsDeleted(true);
            user.setUserName(user.getUserName()+"-"+user.getId());
            userRepository.save(user);
        }


    }

    @Override
    public List<UserDTO> listAllByRole(String role) {

        List<User> users = userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role, false);

        return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    private boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()) {

            case "Manager":
                List<ProjectDTO> projectDTOList =
                        projectService.listAllNonCompletedByAssignedManager(
                                userMapper.convertToDto(user));
                return projectDTOList.size() == 0;

            case "Employee":
                List<TaskDTO> taskDTOList =
                        taskService.listAllNonCompletedByAssignedEmployee(
                                userMapper.convertToDto(user));
                return taskDTOList.size() == 0;
            default:
                return true;


        }


    }



}
