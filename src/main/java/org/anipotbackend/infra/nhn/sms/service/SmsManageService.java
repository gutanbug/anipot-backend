package org.anipotbackend.infra.nhn.sms.service;

import org.anipotbackend.infra.nhn.sms.model.SmsType;
import org.anipotbackend.infra.nhn.sms.model.entity.SmsAuth;

public interface SmsManageService {
    void sendSmsCode(String phoneNumber, SmsType type);

    void verifySmsCode(String phoneNumber, String code, SmsType smsType);

    SmsAuth getSmsAuth(String validatedPhoneNumber, SmsType smsType);
}
