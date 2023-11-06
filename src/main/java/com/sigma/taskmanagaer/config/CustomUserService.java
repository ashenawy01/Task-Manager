package com.sigma.taskmanagaer.config;

import com.sigma.taskmanagaer.entity.Staff;
import com.sigma.taskmanagaer.exp.UserNotFoundException;
import com.sigma.taskmanagaer.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final StaffRepository staffRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Staff staff = staffRepository.findByEmail(username).orElseThrow(() -> new UserNotFoundException("User: " + username + " is NOT FOUND"));

        return UserPrincipal.builder()
                .userId(staff.getStaffId())
                .email(staff.getEmail())
                .password(staff.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(staff.getRole().name())))
                .build();
    }
}
