package com.yasirkhan.em.services.implementations;

import com.yasirkhan.em.exceptions.ResourceNotFoundException;
import com.yasirkhan.em.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository
                .findByUsernameOrEmail(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User with username: " + username + "not found."));

    }
}
