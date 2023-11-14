package com.sigma.taskmanagaer.controller;

import com.sigma.taskmanagaer.dto.ProjectResponse;
import com.sigma.taskmanagaer.dto.StaffRequest;
import com.sigma.taskmanagaer.dto.StaffResponse;
import com.sigma.taskmanagaer.dto.StatisticDTO;
import com.sigma.taskmanagaer.entity.Role;
import com.sigma.taskmanagaer.service.ProjectService;
import com.sigma.taskmanagaer.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final StaffService managerService;
    private final ProjectService projectService;
    private final Role role = Role.ROLE_PROJECT_MANAGER;

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllManager(Pageable pageable) {
        return ResponseEntity.ok(managerService.findAll(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getManagerById(@PathVariable("id") long id) {
        return ResponseEntity.ok(managerService.findById(id, role));
    }


    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectResponse>> getManagerProjects(@PathVariable("id") long id) {
        return ResponseEntity.ok(projectService.findByManagerId(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<?> saveManager(@Valid @ModelAttribute StaffRequest staffRequest, BindingResult result) throws IOException {
        if (result.hasErrors()) {

            // Handle validation errors for each field
            List<FieldError> errors = result.getFieldErrors();
            Map<String, String> validationErrors = new HashMap<>();

            for (FieldError error : errors) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
        }
        return ResponseEntity.ok(managerService.save(staffRequest, role, 0));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateManager(@PathVariable long id,
                                           @Valid @ModelAttribute StaffRequest staffRequest,
                                           BindingResult result) throws IOException {
        if (result.hasErrors()) {

            // Handle validation errors for each field
            List<FieldError> errors = result.getFieldErrors();
            Map<String, String> validationErrors = new HashMap<>();

            for (FieldError error : errors) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
        }
        return ResponseEntity.ok(managerService.save(staffRequest, role, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteManager(@PathVariable long id) {
        managerService.deleteById(id);
        return ResponseEntity.ok("Project Manager has been deleted successfully");
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<StatisticDTO> getStatistics(@PathVariable long id) {
        return ResponseEntity.ok(projectService.getProjectsStatistics(id));
    }


}
