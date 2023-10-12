package com.sigma.taskmanagaer.service;


import com.sigma.taskmanagaer.controller.ManagerController;
import com.sigma.taskmanagaer.controller.ProjectController;
import com.sigma.taskmanagaer.controller.TaskController;
import com.sigma.taskmanagaer.dto.ProjectDTO;
import com.sigma.taskmanagaer.dto.ProjectResponse;
import com.sigma.taskmanagaer.entity.Project;
import com.sigma.taskmanagaer.entity.Staff;
import com.sigma.taskmanagaer.repository.ProjectRepository;
import com.sigma.taskmanagaer.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final StaffRepository staffRepository;

    public Page<ProjectResponse> findAll(Pageable pageable) {
        Page<Project> projects = projectRepository.findAll(pageable);
        return projects.map(this::mapToProjectResponseWithLinks);
    }

    public ProjectResponse findByID(long id) {
        Optional<Project> project = projectRepository.findById(id);
        return project.map(this::mapToProjectResponseWithLinks).orElse(null);
    }

    public ProjectResponse save(ProjectDTO projectDTO) {

        Staff staff =
                staffRepository.findById((long) projectDTO.getManagerID()).orElseThrow(RuntimeException::new);

        Project project =
                Project.builder()
                        .projectId(0L)
                        .manager(staff)
                        .title(projectDTO.getTitle())
                        .description(projectDTO.getDescription())
                        .deadline(projectDTO.getDeadline())
                        .build();

        Project savedProject = projectRepository.save(project);

        return mapToProjectResponseWithLinks(savedProject);
    }

    public ProjectResponse update(ProjectDTO projectDTO, long id) {

        Staff staff =
                staffRepository.findById((long) projectDTO.getManagerID()).orElseThrow(RuntimeException::new);

        Project project =
                Project.builder()
                        .projectId(id)
                        .manager(staff)
                        .title(projectDTO.getTitle())
                        .description(projectDTO.getDescription())
                        .deadline(projectDTO.getDeadline())
                        .build();

        Project savedProject = projectRepository.save(project);

        return mapToProjectResponseWithLinks(savedProject);
    }

    public void deleteByID(long id) {
        projectRepository.deleteById(id);
    }

    private ProjectResponse mapToProjectResponseWithLinks(Project project) {

        ProjectResponse projectResponse =
                ProjectResponse.builder()
                        .projectId(project.getProjectId())
                        .title(project.getTitle())
                        .description(project.getDescription())
                        .deadline(project.getDeadline())
                        .build();

        // Add links to the response
        projectResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjectController.class).getProject(project.getProjectId())).withSelfRel());
        projectResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjectController.class).getProject(project.getProjectId())).withRel("project"));
        projectResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TaskController.class).getTasks(project.getProjectId())).withRel("tasks"));
        projectResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ManagerController.class).getManager(project.getProjectId())).withRel("manager"));

        return projectResponse;
    }
}
