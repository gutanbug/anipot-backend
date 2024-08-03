package org.anipotbackend.domain.user.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidPhoneException extends LocalizedMessageException {

    public InvalidPhoneException() {
        super(HttpStatus.BAD_REQUEST, "invalid.phone");
    }
}
