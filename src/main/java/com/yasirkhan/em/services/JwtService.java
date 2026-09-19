package com.yasirkhan.em.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String generateJwtToken(String username, Map<String, Object> claims);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);

    UserDetails loadUserByUsername(String username);
}
