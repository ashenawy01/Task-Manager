package com.sigma.taskmanagaer.controller;

import com.sigma.taskmanagaer.dto.LoginRequest;
import com.sigma.taskmanagaer.dto.LoginResponse;
import com.sigma.taskmanagaer.dto.ResetPasswordDTO;
import com.sigma.taskmanagaer.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        return authService.attemptLogin(request);
    }

    @GetMapping("/forgot_password")
    public String processForgotPassword(@RequestParam String email, HttpServletRequest request) throws Exception{

        authService.resetPassword(request, email);

        return "";
    }


    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO, BindingResult result) {
        if (result.hasErrors()) {

            // Handle validation errors for each field
            List<FieldError> errors = result.getFieldErrors();
            Map<String, String> validationErrors = new HashMap<>();

            for (FieldError error : errors) {
                validationErrors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors);
        }
        authService.updatePassword(resetPasswordDTO);
        return ResponseEntity.ok("new password has been reset successfully");
    }
}
