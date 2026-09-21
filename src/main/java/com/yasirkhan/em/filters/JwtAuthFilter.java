package com.yasirkhan.em.filters;

import com.yasirkhan.em.exceptions.TokenNotFoundException;
import com.yasirkhan.em.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthFilter(JwtService jwtService, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtService = jwtService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        String method = request.getMethod();

        // For only testing until the Authorization is not added
        if (path.equals("/api/v1/employees") && method.equals("POST")) {
            return true;
        }
        
        return path.startsWith("/api/v1/auth") ||
                path.startsWith("/login") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/swagger-ui.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = "";
        String username = "";
        try {

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new TokenNotFoundException("Missing Access Token");
            }

            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = jwtService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {

                    // Set Spring SecurityContextHolder
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    /*
                     *This line takes the raw HTTP request and extracts extra network details—specifically the user's
                     * IP Address and Session ID—and staples them to the back of the authToken badge.If an error happens
                     * later, or if we want to log user activity, we will know exactly which IP address this request came from.
                     */
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }
}
/*

    OncePerRequestFilter is an abstract base class provided by the Spring Framework that guarantees a filter's core logic is executed exactly once per client request.

The Problem It Solves
In the standard Java Servlet specification, a standard Filter can actually be invoked multiple times during a single HTTP request. This happens if the request undergoes internal routing, such as:

An internal forward to another servlet or controller.

An error dispatch (routing the user to an error page).

An async dispatch (when using asynchronous request processing).

If you use a standard Filter for tasks like authentication or logging, these internal dispatches would cause your filter to run twice. You might end up parsing a JWT twice, logging the same request twice, or throwing duplicate token errors.

OncePerRequestFilter solves this by setting a specific "already filtered" attribute on the request object. Before running your logic, it checks for this attribute. If it exists, it skips execution and continues the filter chain.

Common Use Cases
Because it provides a predictable execution lifecycle, it is the standard choice in Spring Boot for:

Authentication/Authorization: Validating JWTs or session tokens (e.g., JwtAuthenticationFilter).

Logging and Tracing: Generating unique request IDs (Correlation IDs) or logging request payloads.

CORS & Security Headers: Injecting headers that should only be added once.

Context Setup: Setting up thread-local variables like SecurityContextHolder or user locale.

Key Methods to Override
While doFilterInternal is the only required method, OncePerRequestFilter provides several other useful methods you can override to fine-tune its behavior:

shouldNotFilter(HttpServletRequest request): Allows you to explicitly skip the filter for certain endpoints. For example, you can return true if the request URI is /login or /register to avoid parsing tokens on public endpoints.

shouldNotFilterAsyncDispatch(): Defaults to true. This means the filter will not trigger again when the async thread wakes up to complete the request.

shouldNotFilterErrorDispatch(): Defaults to true. This prevents the filter from re-running if Spring throws an exception and routes the request to the /error controller.

 */