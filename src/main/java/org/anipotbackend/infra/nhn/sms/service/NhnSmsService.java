package org.anipotbackend.infra.nhn.sms.service;

import org.anipotbackend.infra.nhn.sms.model.entity.SmsAuth;

public interface NhnSmsService {
    void sendSms(String phone, String smsVerificationCode);
    void saveOrUpdate(SmsAuth smsAuth);
    void updateToChecked(SmsAuth smsAuth);
}
