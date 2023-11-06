package com.sigma.taskmanagaer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties("security.jwt")
public class JwtProperties {
    private String securityKey;
    private int expireTime;
    private String claimEmail;
    private String claimRole;
}
