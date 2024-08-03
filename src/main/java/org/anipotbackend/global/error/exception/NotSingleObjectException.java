package org.anipotbackend.global.error.exception;

import org.springframework.http.HttpStatus;

public class NotSingleObjectException extends LocalizedMessageException {

    public NotSingleObjectException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "cannot.find-single-object");
    }
}
