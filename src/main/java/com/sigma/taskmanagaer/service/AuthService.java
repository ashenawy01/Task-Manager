package com.sigma.taskmanagaer.service;

import com.sigma.taskmanagaer.config.UserPrincipal;
import com.sigma.taskmanagaer.dto.LoginRequest;
import com.sigma.taskmanagaer.dto.LoginResponse;
import com.sigma.taskmanagaer.dto.ResetPasswordDTO;
import com.sigma.taskmanagaer.entity.Role;
import com.sigma.taskmanagaer.entity.Staff;
import com.sigma.taskmanagaer.exp.UserNotFoundException;
import com.sigma.taskmanagaer.repository.StaffRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public LoginResponse attemptLogin(LoginRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);

        var principal = (UserPrincipal) authentication.getPrincipal();

        var roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Staff staff = staffRepository.findById(principal.getUserId()).orElseThrow();

        var token = jwtService.issueToken(staff.getStaffId(), staff.getEmail(), staff.getFirstName(), staff.getImgSrc(), roles);

        return LoginResponse.builder()
                .token(token)
                .build();


    }
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    public void resetPassword(HttpServletRequest request, String email) throws UserNotFoundException, MessagingException {
        Staff staff = staffRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Could not find any staff with the email " + email));
            // Generate Random token
            String token = jwtService.issueToken(staff.getStaffId(), staff.getEmail(), staff.getFirstName(), staff.getImgSrc(), List.of(Role.ROLE_RESET_PASSWORD.name()));

            String link = getSiteURL(request) + "/reset_password?token=" + token;;
            emailService.sendEmail(email, link);


            // assign token to staff with teh given email
            staff.setResetPasswordToken(token);

            // update token in DB
            staffRepository.save(staff);
    }

    public void updatePassword(ResetPasswordDTO resetPasswordDTO) {

        Optional<Staff> staff = staffRepository.findByResetPasswordToken(resetPasswordDTO.getToken());
        if (staff.isEmpty()) {
            throw new UserNotFoundException("User with token provided not found");
        }

        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getNewPassword());
        staff.get().setPassword(encodedPassword);
        staff.get().setResetPasswordToken(null);
        staffRepository.save(staff.get());
    }
}
