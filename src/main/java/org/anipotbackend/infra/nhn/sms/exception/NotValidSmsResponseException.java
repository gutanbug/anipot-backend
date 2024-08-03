package org.anipotbackend.infra.nhn.sms.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotValidSmsResponseException extends LocalizedMessageException {

    public NotValidSmsResponseException() {
        super(HttpStatus.BAD_REQUEST, "invalid.sms-response");
    }
}
