package com.yasirkhan.em.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // Redirect back to your frontend with the error message
        // Example: myapp://login?error=access_denied
        String errorMessage = exception.getMessage();
        // response.sendRedirect("myapp://login?error=" + errorMessage);

        // OR, if you just want to return a JSON response for now:
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{ \"error\": \"" + errorMessage + "\" }");
    }
}