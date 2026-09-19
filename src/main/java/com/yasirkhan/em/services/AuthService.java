package com.yasirkhan.em.services;

import com.yasirkhan.em.dtos.AuthRequest;
import com.yasirkhan.em.dtos.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest request);
}
