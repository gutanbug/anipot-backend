package org.anipotbackend.global.error.exception;

import org.springframework.http.HttpStatus;

public class notValidRefreshTokenException extends LocalizedMessageException{

    public notValidRefreshTokenException() {
        super(HttpStatus.FORBIDDEN, "invalid.refresh-token");
    }
}
