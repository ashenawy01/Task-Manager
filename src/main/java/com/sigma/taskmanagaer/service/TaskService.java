package com.sigma.taskmanagaer.service;

import com.sigma.taskmanagaer.controller.EmployeeController;
import com.sigma.taskmanagaer.controller.ManagerController;
import com.sigma.taskmanagaer.controller.TaskController;
import com.sigma.taskmanagaer.dto.TaskRequest;
import com.sigma.taskmanagaer.dto.TaskResponse;
import com.sigma.taskmanagaer.entity.*;
import com.sigma.taskmanagaer.exp.UserNotFoundException;
import com.sigma.taskmanagaer.repository.ProjectRepository;
import com.sigma.taskmanagaer.repository.StaffRepository;
import com.sigma.taskmanagaer.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final StaffRepository staffRepository;
    private final ProjectRepository projectRepository;
    private final FileService fileService;

    public List<TaskResponse> findAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(this::mapToTaskResponse).toList();
    }
    public TaskResponse findById (long id) {
        Optional<Task> task = taskRepository.findById(id);

        return task.map(this::mapToTaskResponse).orElse(null);
    }

    public TaskResponse save(TaskRequest taskRequest, long id) {

        Optional<Task> foundTask = taskRepository.findById(id);
        if (foundTask.isPresent() && foundTask.get().getStatus() != Status.To_Do) {
            throw new IllegalArgumentException("Task Cannot be updated - already started!");
        }

        Project project = projectRepository.findById(taskRequest.getProjectId())
                .orElseThrow(() -> new UserNotFoundException("Project ID [ " + id + " ] is NOT FOUND"));

        Staff employee = staffRepository.findByStaffIdAndRole(taskRequest.getEmployeeId(), Role.ROLE_EMPLOYEE).
                orElseThrow(() -> new UserNotFoundException("Employee ID [ " + id + " ] is NOT FOUND"));

        Task task = Task.builder()
                .taskId(id)
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .project(project)
                .deadline(taskRequest.getDeadline())
                .status(Status.To_Do)
                .employee(employee)
                .build();

        Task savedTask = taskRepository.save(task);

        return mapToTaskResponse(savedTask);
    }

    public void deleteById(long id) {
        taskRepository.deleteById(id);
    }
    public List<TaskResponse> findEmpTasks( long id, Role role) {
        Staff employee = staffRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Employee with id " + id + " NOT FOUND"));
        List<Task> tasks = taskRepository.findByEmployee(employee);
        return tasks.stream().map(this::mapToTaskResponse).toList();
    }
    public List<TaskResponse> findByProjectId(long projId) {
        Project project = projectRepository.findById(projId)
                .orElseThrow(() -> new UserNotFoundException("Project ID [ " + projId + " ] is NOT FOUND"));

        List<Task> tasks = taskRepository.findByProject(project);
        return tasks.stream().map(this::mapToTaskResponse).toList();
    }

    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse taskResponse =
                TaskResponse.builder()
                        .id(task.getTaskId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .startDate(task.getStartDate())
                        .deadline(task.getDeadline())
                        .status(task.getStatus())
                        .employeeId(task.getEmployee().getStaffId())
                        .employeeName(task.getEmployee().getFirstName())
                        .employeeImg(task.getEmployee().getImgSrc())
                        .build();

        taskResponse.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TaskController.class)
                        .getTask(task.getTaskId())).withSelfRel());

        taskResponse.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(EmployeeController.class).
                        getEmployeeById(task.getEmployee().getStaffId())).withRel("employee"));

        taskResponse.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(ManagerController.class).
                        getManagerById(task.getProject().getManager().getStaffId())).withRel("manager"));

        return taskResponse;
    }

    public TaskResponse startTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Task ID [ " + id + " ] is NOT FOUND"));

        if (task.getStatus() == Status.Done) {
            throw new IllegalArgumentException("Task is already Done!");
        }

        task.setStatus(Status.In_Progress);
        task.setStartDate(LocalDateTime.now());
        task = taskRepository.save(task);
        return mapToTaskResponse(task);
    }

    public TaskResponse uploadTask(MultipartFile multipartFile, long id) throws IOException {

        Task task = taskRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Task ID [ " + id + " ] is NOT FOUND"));

        if (task.getStatus() == Status.Done) {
            throw new IllegalArgumentException("Task is already Done!");
        }

        task.setSubmitFile(fileService.saveFile(multipartFile));
        task.setStatus(Status.Done);
        taskRepository.save(task);
        return mapToTaskResponse(task);
    }
}
