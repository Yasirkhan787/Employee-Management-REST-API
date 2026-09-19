package com.yasirkhan.em.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends RuntimeException{

    private final HttpStatus status;

    public TokenExpiredException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
