package org.anipotbackend.infra.nhn.sms.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class SmsCodeNotValidException extends LocalizedMessageException {

    public SmsCodeNotValidException() {
        super(HttpStatus.BAD_REQUEST, "invalid.sms-code");
    }
}
