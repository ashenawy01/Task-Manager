package com.sigma.taskmanagaer.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sigma.taskmanagaer.config.JwtProperties;
import com.sigma.taskmanagaer.config.UserPrincipal;
import com.sigma.taskmanagaer.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties properties;
    public String issueToken(long id, String email, List<String> roles) {

        return JWT.create()
                .withExpiresAt(Instant.now().plus(Duration.of(properties.getExpireTime(), ChronoUnit.DAYS)))
                .withSubject(String.valueOf(id))
                .withClaim(properties.getClaimEmail(), email)
                .withClaim(properties.getClaimRole(), roles)
                .sign(Algorithm.HMAC256(properties.getSecurityKey()));
    }

    public DecodedJWT decodeToken(String token) {
        return JWT.require(Algorithm.HMAC256(properties.getSecurityKey()))
                .build().verify(token);
    }

    public UserPrincipal convertTokenToPrincipal(DecodedJWT jwt) {
        return UserPrincipal.builder()
                .userId(Long.valueOf(jwt.getSubject()))
                .email(jwt.getClaim(properties.getClaimEmail()).asString())
                .authorities(extractAuthorities(jwt))
                .isExpired(isTokenExpired(jwt))
                .build();
    }

    private boolean isTokenExpired(DecodedJWT jwt) {
        final Date expireDate =  jwt.getExpiresAt();
        return expireDate.before(new Date());
    }

    private List<SimpleGrantedAuthority> extractAuthorities(DecodedJWT jwt) {
        var claim = jwt.getClaim(properties.getClaimRole());
        if (claim.isNull() || claim.isMissing()) {
            return List.of();
        }
        return claim.asList(SimpleGrantedAuthority.class);
    }
}
