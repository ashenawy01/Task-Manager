package com.sigma.taskmanagaer.dto;


import lombok.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProjectResponse extends RepresentationModel<ProjectResponse> {
    private Long projectId;
    private String title;
    private String description;
    private LocalDateTime deadline;
}
