package com.sigma.taskmanagaer.dto;

import com.sigma.taskmanagaer.entity.Status;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TaskResponse extends RepresentationModel<TaskResponse> {

    private int id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Status status;
    private String employeeName;
    private String employeeImg;
}
