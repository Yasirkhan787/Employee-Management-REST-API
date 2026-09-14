package com.yasirkhan.em.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExist extends RuntimeException{

    private final HttpStatus status;

    public ResourceAlreadyExist(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
