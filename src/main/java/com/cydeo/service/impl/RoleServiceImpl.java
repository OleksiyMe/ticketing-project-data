package com.cydeo.service.impl;

import com.cydeo.dto.RoleDTO;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.RoleMapper;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    private final MapperUtil mapperUtil;


    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper, MapperUtil mapperUtil) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public List<RoleDTO> listAllRoles() {

        //Controller called me and requesting all RoleDTOs to show in drop-down
        // I need to make a call to DB and get all the roles from table
        //go to repo and find a service wich gives me a roles from DB
        // So DI the repo

        List<Role> roleList =roleRepository.findAll();
        //Then we need to convert Role_s To RoleDTO_s
        //Use mappers (ModelMapper to convert) . We created RoleMapper and use stream
        // to get our return


       // return roleList.stream().map(roleMapper::convertToDto).collect(Collectors.toList());

        return roleList.stream().map(role->mapperUtil.convert(role,
                new RoleDTO())).collect(Collectors.toList());

//        return roleList.stream().map(role->mapperUtil.convert(role,
//                RoleDTO.class)).collect(Collectors.toList());
//

    }

    @Override
    public RoleDTO findById(Long id) {

        Role role = roleRepository.findById(id).get();

        return roleMapper.convertToDto(role);
    }
}
