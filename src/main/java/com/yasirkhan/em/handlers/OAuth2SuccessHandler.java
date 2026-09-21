package com.yasirkhan.em.handlers;

import com.yasirkhan.em.entities.Employee;
import com.yasirkhan.em.entities.User;
import com.yasirkhan.em.repositories.EmployeeRepository;
import com.yasirkhan.em.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;

    public OAuth2SuccessHandler(JwtService jwtService, EmployeeRepository employeeRepository) {
        this.jwtService = jwtService;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        if (oAuth2User != null) {
            String  email = oAuth2User.getAttribute("email");

            if (email == null) {
                throw new RuntimeException("Email is not provided by the provider");
            }

            User user = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found after OAuth2 login"))
                    .getUser();

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole());
            claims.put("userId", user.getId());

            String token = jwtService.generateJwtToken(email, claims);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\n  \"message\": \"Authentication Successful\",\n  \"token\": \"" + token + "\"\n}");
            response.getWriter().flush();
        }
    }
}
