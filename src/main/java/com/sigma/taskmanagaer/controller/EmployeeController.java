package com.sigma.taskmanagaer.controller;


import com.sigma.taskmanagaer.dto.ProjectResponse;
import com.sigma.taskmanagaer.dto.StaffRequest;
import com.sigma.taskmanagaer.dto.StaffResponse;
import com.sigma.taskmanagaer.dto.TaskResponse;
import com.sigma.taskmanagaer.entity.Role;
import com.sigma.taskmanagaer.service.ProjectService;
import com.sigma.taskmanagaer.service.StaffService;
import com.sigma.taskmanagaer.service.TaskService;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {


    private final StaffService employeeService;
    private final TaskService taskService;

    private final Role role = Role.ROLE_EMPLOYEE;

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getEmployees() {
        return ResponseEntity.ok(employeeService.findAll(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponse> getEmployeeById(@PathVariable("id") long id) {
        return ResponseEntity.ok(employeeService.findById(id, role));
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> getEmployeeTasks(@PathVariable long id) {
        return ResponseEntity.ok(taskService.findEmpTasks(id, role));
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<?> saveEmployee(@Valid @ModelAttribute StaffRequest staffRequest,
                                                      BindingResult result) throws IOException {
        if (result.hasErrors()) {

            // Handle validation errors for each field
            List<FieldError> errors = result.getFieldErrors();
            Map<String, String> validationErrors = new HashMap<>();

            for (FieldError error : errors) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
        } else {
            // Proceed with saving the employee
            return ResponseEntity.ok(employeeService.save(staffRequest, role, 0));
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable long id, @Valid @ModelAttribute StaffRequest staffRequest, BindingResult result) throws IOException {
        if (result.hasErrors()) {

            // Handle validation errors for each field
            List<FieldError> errors = result.getFieldErrors();
            Map<String, String> validationErrors = new HashMap<>();

            for (FieldError error : errors) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
        }
        //
        return ResponseEntity.ok(employeeService.save(staffRequest, role, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        employeeService.deleteById(id);
        return ResponseEntity.ok("Project employee has been deleted successfully");
    }
}
