package com.sigma.taskmanagaer.controller;

import com.sigma.taskmanagaer.dto.TaskRequest;
import com.sigma.taskmanagaer.dto.TaskResponse;
import com.sigma.taskmanagaer.entity.Status;
import com.sigma.taskmanagaer.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> saveTask(@Valid @ModelAttribute TaskRequest taskRequest,
                                                 BindingResult result) {
        if (result.hasErrors()) {

            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String, String> errorResponse = fieldErrors.stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return ResponseEntity.ok(taskService.save(taskRequest, 0));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@Valid @ModelAttribute TaskRequest taskRequest,
                                      @PathVariable long id,
                                      BindingResult result) {
        if (result.hasErrors()) {

            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String, String> errorResponse = fieldErrors.stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return ResponseEntity.ok(taskService.save(taskRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable long id) {
        taskService.deleteById(id);
        return ResponseEntity.ok("Task was Deleted Successfully");
    }


    @PatchMapping("/{id}/start")
    public ResponseEntity<TaskResponse> startTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.startTask(id));
    }


    @PatchMapping("/{id}/done")
    public ResponseEntity<TaskResponse> doneTask(@PathVariable Long id,
                                                 @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(taskService.uploadTask(file, id));
    }
}
