package com.sigma.taskmanagaer.controller;

import com.sigma.taskmanagaer.dto.LoginRequest;
import com.sigma.taskmanagaer.dto.LoginResponse;
import com.sigma.taskmanagaer.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        return authService.attemptLogin(request);
    }

}
