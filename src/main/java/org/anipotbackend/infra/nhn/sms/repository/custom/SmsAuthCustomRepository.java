package org.anipotbackend.infra.nhn.sms.repository.custom;

import org.anipotbackend.infra.nhn.sms.model.SmsType;
import org.anipotbackend.infra.nhn.sms.model.entity.SmsAuth;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SmsAuthCustomRepository {
    Optional<SmsAuth> findValidSmsAuth(@Param("phone") String phoneNumber, @Param("type") SmsType smsType);
    void updateCode(@Param("phone") String phone, @Param("type") SmsType type, @Param("code") String code, @Param("trial") int trial);
    Optional<SmsAuth> findSmsAuthByCodeForTest(@Param("code") String code);
    Optional<SmsAuth> getSmsAuth(@Param("phoneNumber") String phoneNumber, @Param("type") SmsType type);
    void expiredSmsAuth(SmsAuth smsAuth);
}
