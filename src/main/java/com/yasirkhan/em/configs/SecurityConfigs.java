package com.yasirkhan.em.configs;

import com.yasirkhan.em.filters.JwtAuthFilter;
import com.yasirkhan.em.services.implementations.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfigs {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfigs(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.POST, "/api/v1/employees")
                                .permitAll()// Allow only POST requests to the employees endpoint
                                .requestMatchers("/api/v1/auth/**", "/api-docs", "/swagger-ui.html")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws AuthenticationException {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
/*
    1. Configuration Time (Your @Bean method)
    When you write @Bean public AuthenticationProvider authenticationProvider(), you are not logging a user in.
    You are simply building the machine when the application starts up.
    You are telling Spring: "Here is my authentication engine (the DaoAuthenticationProvider).
    I have attached the database fetcher (UserDetailsService) and the password checker (PasswordEncoder) to it.
    Please hold onto this machine." You return the provider itself so Spring can store it in its context.

    2. Runtime Execution (When a user actually logs in)
    When a client sends an HTTP request with Basic Auth credentials, the following happens automatically
    under the hood (you do not write this code; Spring does it for you):

    The Spring Security filter extracts the username and password from the request.

    The filter creates an unauthenticated Authentication object (specifically a UsernamePasswordAuthenticationToken)
    containing those raw credentials.

    The filter hands this object to the AuthenticationManager.

    The AuthenticationManager grabs the "machine" you built (your DaoAuthenticationProvider bean) and calls its internal
    authenticate(Authentication auth) method, passing in that token.

    If the credentials are valid, that internal method returns the fully authenticated Authentication object, which is
    then saved in the Security Context.

    So, your @Bean method builds and returns the provider. Later, during a live request, Spring calls a method inside
    that provider which takes and returns the Authentication objects.

    For current Basic Authentication setup, you do not need to manually create an AuthenticationManager bean.
    Spring Security automatically detects your AuthenticationProvider bean and wires it into a default manager behind the scenes.
    However, when we move to our JWT implementation, you will need to expose it as a bean.
    With JWTs, we usually create a custom /login endpoint (e.g., in an AuthController) where we receive the username and
    password from the JSON request body. To verify those credentials in our controller, we have to inject the
    AuthenticationManager and manually call its .authenticate() method. Sor for that we need AuthenticationManager bean.
 */