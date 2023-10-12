package com.sigma.taskmanagaer.dto;


import jakarta.validation.constraints.Min;
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
public class ProjectDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Date is required")
    private LocalDateTime deadline;

    @NotBlank(message = "Manager ID is required")
    @Pattern(regexp = "^[1-9]\\d*$", message = "Please Enter a valid ID Manager (positive integer)")
    private int managerID;

    private String description;
}
