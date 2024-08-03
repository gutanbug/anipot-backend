package org.anipotbackend.global.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

public class NotGrantedException extends LocalizedMessageException {
    public NotGrantedException() {
        super(HttpStatus.FORBIDDEN, "required.granted");
    }

    public NotGrantedException(Throwable t) {
        super(t, HttpStatus.FORBIDDEN, "required.granted");
    }
}
