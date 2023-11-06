package com.sigma.taskmanagaer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.http.HttpRequest;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;



    @Bean
    public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {

        http
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
//                .and()
                .securityMatcher("/**")
                .authorizeHttpRequests(registry -> registry
//                        .requestMatchers("/public/**").permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/managers").hasAnyRole("ADMIN", "PROJECT_MANAGER", "GLOBAL_ADMIN")
                                .requestMatchers("/admins").hasAnyRole("ADMIN", "GLOBAL_ADMIN")
                                .requestMatchers("/employees").hasAnyRole("ADMIN", "GLOBAL_ADMIN", "EMPLOYEE")
                                .requestMatchers("/projects").hasAnyRole("ADMIN", "GLOBAL_ADMIN", "PROJECT_MANAGER")
                                .requestMatchers("/tasks").hasAnyRole("PROJECT_MANAGER")

                                .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
