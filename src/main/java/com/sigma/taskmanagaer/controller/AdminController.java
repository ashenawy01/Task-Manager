package com.sigma.taskmanagaer.controller;

import com.sigma.taskmanagaer.dto.AdminRequest;
import com.sigma.taskmanagaer.dto.AdminResponse;
import com.sigma.taskmanagaer.service.AdminService;
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
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;


    @GetMapping
    public ResponseEntity<List<AdminResponse>> getEmployees() {
        return ResponseEntity.ok(adminService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> getEmployeeById(@PathVariable("id") long id) {
        return ResponseEntity.ok(adminService.findById(id));
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<?> saveEmployee(@Valid @ModelAttribute AdminRequest staffRequest,
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
            return ResponseEntity.ok(adminService.save(staffRequest, 0));
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable long id, @Valid @ModelAttribute AdminRequest staffRequest, BindingResult result) throws IOException {
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
        return ResponseEntity.ok(adminService.save(staffRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable long id) {
        adminService.deleteById(id);
        return ResponseEntity.ok("Project employee has been deleted successfully");
    }

}
