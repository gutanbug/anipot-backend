package org.anipotbackend.domain.user.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class AlreadyUserException extends LocalizedMessageException {

    public AlreadyUserException() {
        super(HttpStatus.BAD_REQUEST, "already.user");
    }
}
