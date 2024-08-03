package org.anipotbackend.domain.user.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class LoginFailedException extends LocalizedMessageException {

    public LoginFailedException() {
        super(HttpStatus.BAD_REQUEST, "failed.login");
    }
}
