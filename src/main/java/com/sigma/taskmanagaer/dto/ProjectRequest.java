package com.sigma.taskmanagaer.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProjectRequest {

    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "Date is required")
    private LocalDateTime deadline;

    @Positive(message = "Please Enter a valid Manager ID (positive integer)")
    private Long managerID;

    private String description;
}


