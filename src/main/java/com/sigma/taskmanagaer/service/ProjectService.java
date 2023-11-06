package com.sigma.taskmanagaer.service;


import com.sigma.taskmanagaer.controller.ManagerController;
import com.sigma.taskmanagaer.controller.ProjectController;
import com.sigma.taskmanagaer.controller.TaskController;
import com.sigma.taskmanagaer.dto.ProjectRequest;
import com.sigma.taskmanagaer.dto.ProjectResponse;
import com.sigma.taskmanagaer.dto.StatisticDTO;
import com.sigma.taskmanagaer.entity.Project;
import com.sigma.taskmanagaer.entity.Staff;
import com.sigma.taskmanagaer.entity.Status;
import com.sigma.taskmanagaer.entity.Task;
import com.sigma.taskmanagaer.repository.ProjectRepository;
import com.sigma.taskmanagaer.repository.StaffRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final StaffRepository staffRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public List<ProjectResponse> findAll() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(this::mapToProjectResponseWithLinks).toList();
    }

    public ProjectResponse findByID(long id) {
        Optional<Project> project = projectRepository.findById(id);
        return project.map(this::mapToProjectResponseWithLinks).orElse(null);
    }

    public List<ProjectResponse> findByManagerId(long id) {

        // Find the manager
        // If No Stuff with the provided ID found, Throw Exception
        Staff staff = staffRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        // Find all manager projects
        List<Project> projects = projectRepository.findByManager(staff);

        // Map and return projects response
        return projects.stream().map(this::mapToProjectResponseWithLinks).toList();
    }

    public ProjectResponse save(ProjectRequest projectRequest) {

        Staff staff =
                staffRepository.findById((long) projectRequest.getManagerID()).orElseThrow(RuntimeException::new);

        Project project =
                Project.builder()
                        .projectId(0L)
                        .manager(staff)
                        .title(projectRequest.getTitle())
                        .description(projectRequest.getDescription())
                        .deadline(projectRequest.getDeadline())
                        .build();

        Project savedProject = projectRepository.save(project);

        return mapToProjectResponseWithLinks(savedProject);
    }

    public ProjectResponse update(ProjectRequest projectRequest, long id) {

        Staff staff =
                staffRepository.findById((long) projectRequest.getManagerID()).orElseThrow(RuntimeException::new);

        Project project =
                Project.builder()
                        .projectId(id)
                        .manager(staff)
                        .title(projectRequest.getTitle())
                        .description(projectRequest.getDescription())
                        .deadline(projectRequest.getDeadline())
                        .build();

        Project savedProject = projectRepository.save(project);

        return mapToProjectResponseWithLinks(savedProject);
    }

    public void deleteByID(long id) {
        projectRepository.deleteById(id);
    }


    public StatisticDTO getProjectsStatistics() {
        Map<String, Integer> dataMap = new HashMap<>();
        int totalProjects = 0, inProgressProjects = 0, doneProjects = 0;

        String jpql = "SELECT " +
                "(SELECT COUNT(p) FROM Project p) AS total, " +
                "(SELECT COUNT(DISTINCT p) FROM Project p " +
                "WHERE (SELECT COUNT(t) FROM Task t " +
                "WHERE t.status != :doneStatus AND t.project = p) > 0) AS inProgress " +
                "FROM Project p";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("doneStatus", Status.Done);

        List<Object[]> results = query.getResultList();

        if (!results.isEmpty()) {
            Object[] result = results.get(0);

            totalProjects = ((Number) result[0]).intValue();
            inProgressProjects =  ((Number) result[1]).intValue();
            doneProjects =  totalProjects - inProgressProjects;

        }

        dataMap.put("total", totalProjects);
        dataMap.put("in_progress", inProgressProjects);
        dataMap.put("done", doneProjects);

        return StatisticDTO.builder()
                .title("projects")
                .data(dataMap)
                .build();
    }




    private ProjectResponse mapToProjectResponseWithLinks(Project project) {


        ProjectResponse projectResponse =
                ProjectResponse.builder()
                        .projectId(project.getProjectId())
                        .title(project.getTitle())
                        .description(project.getDescription())
                        .deadline(project.getDeadline())
                        .managerID(project.getManager().getStaffId())
                        .managerName(project.getManager().getFirstName())
                        .managerImg(project.getManager().getImgSrc())
                        .progress(calcProjectProgress(project))
                        .build();

        // Add links to the response
        projectResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjectController.class).getProject(project.getProjectId())).withSelfRel());
        projectResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjectController.class)
                .getProjectTasks(project.getProjectId())).withRel("tasks"));
        projectResponse.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ManagerController.class).getManagerById(project.getManager().getStaffId())).withRel("manager"));

        return projectResponse;
    }

    private int calcProjectProgress(Project project) {


        if (project.getTasks() == null || project.getTasks().isEmpty()) {
            return 0;
        }

        int allTasks = project.getTasks().size();
        int doneTasks = 0;

        for (Task task : project.getTasks()) {
            if (task.getStatus() == Status.Done) {
                doneTasks++;
            }
        }
        return (doneTasks * 100) / allTasks;

    }
}
