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

    private long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime deadline;
    private Status status;
    private long employeeId;
    private String employeeName;
    private String employeeImg;
}
