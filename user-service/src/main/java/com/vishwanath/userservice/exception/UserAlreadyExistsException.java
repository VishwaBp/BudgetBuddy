package com.vishwanath.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exception thrown when a user attempts to register with a username that already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Returns a 409 status code when this exception is thrown
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

