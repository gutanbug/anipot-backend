package org.anipotbackend.domain.user.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidNicknameException extends LocalizedMessageException {

    public InvalidNicknameException() {
        super(HttpStatus.BAD_REQUEST, "invalid.nickname");
    }
}
