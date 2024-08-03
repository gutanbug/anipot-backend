package org.anipotbackend.domain.user.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends LocalizedMessageException {

    public InvalidPasswordException() {
        super(HttpStatus.BAD_REQUEST, "invalid.password");
    }
}
