package com.sigma.taskmanagaer.dto;


import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AdminResponse extends RepresentationModel<AdminResponse> {
    private Long staffId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private String imgSrc;
    private boolean isGlobal;
}
