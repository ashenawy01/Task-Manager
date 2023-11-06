package com.sigma.taskmanagaer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AdminRequest {

    @NotBlank(message = "First Name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Please enter a valid First Name")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Please enter a valid Last Name")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid E-mail")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Please enter a valid password")
    private String password;

    @NotBlank(message = "Phone No is required")
    @Pattern(regexp = "^01[0-9]{9}$", message = "Invalid Phone No! Please enter 11 digits starting with 01 (e.g. '01234567890')")
    private String phoneNo;

    private boolean isGlobal;
    private MultipartFile img;
}
