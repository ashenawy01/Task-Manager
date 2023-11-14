package com.sigma.taskmanagaer.controller;

import com.sigma.taskmanagaer.dto.ProjectRequest;
import com.sigma.taskmanagaer.dto.ProjectResponse;
import com.sigma.taskmanagaer.dto.StatisticDTO;
import com.sigma.taskmanagaer.dto.TaskResponse;
import com.sigma.taskmanagaer.service.ProjectService;
import com.sigma.taskmanagaer.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable long id) {
        return ResponseEntity.ok(projectService.findByID(id));
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(@PathVariable long id) {
        return ResponseEntity.ok(taskService.findByProjectId(id));
    }

    @PostMapping
    public ResponseEntity<?> SaveProject(@Valid @RequestBody ProjectRequest projectRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            List<FieldError> errors = bindingResult.getFieldErrors();

            Map<String, String> errorResponse = new HashMap<>();

            for (FieldError error : errors) {
                errorResponse.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return ResponseEntity.ok(projectService.save(projectRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> UpdateProject(@PathVariable long id,
                                           @Valid @RequestBody ProjectRequest projectRequest,
                                           BindingResult bindingResult) {

        System.out.println("in update");


        if (bindingResult.hasErrors()) {

            List<FieldError> errors = bindingResult.getFieldErrors();

            Map<String, String> errorResponse = errors.stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return ResponseEntity.ok(projectService.update(projectRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable long id) {
        projectService.deleteByID(id);
        return ResponseEntity.ok("Project has been deleted successfully");
    }
    @GetMapping("/statistics")
    public ResponseEntity<StatisticDTO> getStatistics() {
        return ResponseEntity.ok(projectService.getProjectsStatistics());
    }


}
