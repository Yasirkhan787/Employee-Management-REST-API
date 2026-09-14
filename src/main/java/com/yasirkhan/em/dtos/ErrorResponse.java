package com.yasirkhan.em.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(int statusCode, String message, LocalDateTime timestamp) {}