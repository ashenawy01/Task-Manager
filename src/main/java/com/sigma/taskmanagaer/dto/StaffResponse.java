package com.sigma.taskmanagaer.dto;


import com.sigma.taskmanagaer.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class StaffResponse extends RepresentationModel<StaffResponse> {
    private Long staffId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private String imgSrc;
}
