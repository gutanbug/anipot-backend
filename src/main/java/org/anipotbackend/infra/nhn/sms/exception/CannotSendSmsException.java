package org.anipotbackend.infra.nhn.sms.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class CannotSendSmsException extends LocalizedMessageException {

    public CannotSendSmsException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "failed.send-sms");
    }
}
