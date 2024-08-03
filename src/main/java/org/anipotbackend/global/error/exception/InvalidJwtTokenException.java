package org.anipotbackend.global.error.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtTokenException extends LocalizedMessageException{

    public InvalidJwtTokenException() {
        super(HttpStatus.FORBIDDEN, "invalid.token");
    }
}
