package org.anipotbackend.domain.user.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidEmailException extends LocalizedMessageException {

    public InvalidEmailException() {
        super(HttpStatus.BAD_REQUEST, "invalid.email");
    }
}
