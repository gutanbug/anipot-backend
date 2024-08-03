package org.anipotbackend.infra.nhn.sms.exception;

import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class SmsAuthNotFoundException extends LocalizedMessageException {

    public SmsAuthNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.sms-auth");
    }
}
