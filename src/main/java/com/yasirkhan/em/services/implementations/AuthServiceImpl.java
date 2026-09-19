package com.yasirkhan.em.services.implementations;

import com.yasirkhan.em.dtos.AuthRequest;
import com.yasirkhan.em.dtos.AuthResponse;
import com.yasirkhan.em.entities.User;
import com.yasirkhan.em.exceptions.BadCredentialsException;
import com.yasirkhan.em.exceptions.ResourceNotFoundException;
import com.yasirkhan.em.repositories.UserRepository;
import com.yasirkhan.em.services.AuthService;
import com.yasirkhan.em.services.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {

        Authentication authentication = null;

        try {
            authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.username(),
                                    request.password()
                            )
                    );

            User user = (User) authentication.getPrincipal();

            String role = user.getRole().name();

            String userId = user.getId().toString();

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);
            claims.put("userId", userId);

            return new
                    AuthResponse(
                    jwtService
                            .generateJwtToken(user.getUsername(), claims));
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException(exception.getMessage());
        }

    }
}
