package com.sigma.taskmanagaer.service;

import com.sigma.taskmanagaer.config.UserPrincipal;
import com.sigma.taskmanagaer.dto.LoginRequest;
import com.sigma.taskmanagaer.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse attemptLogin(LoginRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);

        var principal = (UserPrincipal) authentication.getPrincipal();

        var roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var token = jwtService.issueToken(principal.getUserId(), principal.getEmail(), roles);

        return LoginResponse.builder()
                .token(token)
                .build();


    }
}
