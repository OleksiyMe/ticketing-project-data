package com.cydeo.mapper;

import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Role;
import com.cydeo.entity.User;
import org.modelmapper.ModelMapper;

public class UserMapper{

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User convertToEntity(UserDTO dto){

        return  modelMapper.map(dto, User.class);
    };


    public UserDTO convertToDto(Role entity){

        return  modelMapper.map(entity,UserDTO.class);
    };

}
