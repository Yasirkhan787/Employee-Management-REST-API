package com.yasirkhan.em.exceptions;

import org.springframework.http.HttpStatus;

public class BadCredentialsException extends RuntimeException{

    private final HttpStatus status;

    public BadCredentialsException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
