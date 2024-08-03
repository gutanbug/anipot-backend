package org.anipotbackend.infra.nhn.sms.exception;

import org.anipotbackend.global.error.CustomHttpStatus;
import org.anipotbackend.global.error.exception.LocalizedMessageException;

public class TooManyFailedSmsRequestException extends LocalizedMessageException {

    public TooManyFailedSmsRequestException() {
        super(CustomHttpStatus.BANNED_REQUEST, "failed.banned-phone");
    }
}
