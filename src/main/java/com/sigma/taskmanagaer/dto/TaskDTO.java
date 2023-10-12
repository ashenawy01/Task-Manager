package com.sigma.taskmanagaer.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskDTO {

    @NotBlank(message = "Project Title is required")
    private String title;

    private String description;

    @NotNull(message = "Date is required")
    private LocalDateTime deadline;

    @NotBlank(message = "Project ID is missing")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Please Enter a valid Project ID (positive integer)")
    private int projectId;

    @NotBlank(message = "Employee ID is required")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Please Enter a valid Employee ID  (positive integer)")
    private int employeeId;

}
