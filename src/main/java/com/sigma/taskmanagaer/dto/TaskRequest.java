package com.sigma.taskmanagaer.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskRequest {

    @NotBlank(message = "Project Title is required")
    private String title;

    private String description;

    @NotNull(message = "Date is required")
    private LocalDateTime deadline;

    @NotNull(message = "Project ID is missing")
    @Positive(message = "Please Enter a valid Project ID (positive integer)")
    private Long projectId;

    @NotNull(message = "Employee ID is required")
    @Positive(message = "Please Enter a valid Employee ID (positive integer)")
    private Long employeeId;

}
