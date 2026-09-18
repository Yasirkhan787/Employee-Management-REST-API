package com.yasirkhan.em.controllers;

import com.yasirkhan.em.dtos.AuthRequest;
import com.yasirkhan.em.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        service.authenticate(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
