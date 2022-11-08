package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.Project;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import com.cydeo.mapper.ProjectMapper;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.ProjectRepository;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {


    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectMapper projectMapper, UserService userService, UserMapper userMapper, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMapper = projectMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.taskService = taskService;
    }

    @Override
    public ProjectDTO getByProjectCode(String code) {

        Project project = projectRepository.findByProjectCode(code);

        return projectMapper.convertToDto(project);
    }

    @Override
    public List<ProjectDTO> listAllProjects() {

        List<Project> projectList = projectRepository.findAll(Sort.by("projectCode"));
        return projectList.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {

        dto.setProjectStatus(Status.OPEN);
        Project project = projectMapper.convertToEntity(dto);
//        User assignedManager= userRepository.findByUserName(project.getAssignedManager().getUserName());
//        project.setAssignedManager(assignedManager);
        projectRepository.save(project);

    }

    @Override
    public void update(ProjectDTO dto) {
        Project projectUnchanged = projectRepository.findByProjectCode(dto.getProjectCode());
        Project changedProject = projectMapper.convertToEntity(dto);
        changedProject.setId(projectUnchanged.getId());
        changedProject.setProjectStatus(projectUnchanged.getProjectStatus());

        projectRepository.save(changedProject);

    }

    @Override
    public void delete(String code) {

        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);

        project.setProjectCode(project.getProjectCode()+"-"+project.getId());  //

        projectRepository.save(project);
        taskService.deleteByProject(projectMapper.convertToDto(project));


    }

    @Override
    public void complete(String projectCode) {
        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);

        taskService.completeByProject(projectMapper.convertToDto(project));

    }

    @Override
    public List<ProjectDTO> listAllProjectDetails() {

        UserDTO curentUserDTO = userService.findByUserName("harold@manager.com");
        User user = userMapper.convertToEntity(curentUserDTO);

        List<Project> projectList = projectRepository.findAllByAssignedManager(user);

        return projectList.stream().map(eachProject -> {

            ProjectDTO obj = projectMapper.convertToDto(eachProject);
            obj.setUnfinishedTaskCounts(
                    taskService.totalNonCompletedTask(eachProject.getProjectCode()));   //Entity does not have these fields
            obj.setCompleteTaskCounts(
                    taskService.totalCompletedTask(eachProject.getProjectCode())); //Entity does not have these fields
            return obj;

        }).collect(Collectors.toList());


    }

    @Override
    public List<ProjectDTO> listAllNonCompletedByAssignedManager(UserDTO assignedManager) {


        List<Project> projects =projectRepository.findAllByProjectStatusIsNotAndAssignedManager(
                Status.COMPLETE, userMapper.convertToEntity(assignedManager)
        );

        return projects.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }
}
