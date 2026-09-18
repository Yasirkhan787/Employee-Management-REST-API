package com.yasirkhan.em.services;

import com.yasirkhan.em.dtos.AuthRequest;

public interface AuthService {

    void authenticate(AuthRequest request);
}
