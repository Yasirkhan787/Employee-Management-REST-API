package com.yasirkhan.em.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
        @NotBlank(message = "Username cannot be empty")
        String username,
        @NotBlank(message = "Password cannot be empty")
        String password) {
}
