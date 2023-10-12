package com.sigma.taskmanagaer.dto;

import com.sigma.taskmanagaer.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.awt.font.MultipleMaster;

public class StaffDTO {

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
    @Pattern(regexp = "^[1-9][0-9]{9}$", message = "Invalid Phone No! Please enter 10 digits starting with 1 (e.g. '1234567890')")
    private String phoneNo;

    private MultipleMaster img;
}
