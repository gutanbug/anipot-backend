package org.anipotbackend.infra.nhn.sms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.domain.user.exception.AlreadyUserException;
import org.anipotbackend.domain.user.model.entity.User;
import org.anipotbackend.domain.user.repository.UserRepository;
import org.anipotbackend.infra.nhn.sms.exception.SmsAuthNotFoundException;
import org.anipotbackend.infra.nhn.sms.exception.SmsCodeNotValidException;
import org.anipotbackend.infra.nhn.sms.model.SmsStatus;
import org.anipotbackend.infra.nhn.sms.model.SmsType;
import org.anipotbackend.infra.nhn.sms.model.entity.SmsAuth;
import org.anipotbackend.infra.nhn.sms.repository.SmsAuthRepository;
import org.anipotbackend.infra.nhn.sms.service.NhnSmsService;
import org.anipotbackend.infra.nhn.sms.service.SmsManageService;
import org.anipotbackend.infra.nhn.sms.util.CodeGenerator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsManageServiceImpl implements SmsManageService {

    private static final int digitLength = 6;

    private final NhnSmsService nhnSmsService;
    private final SmsAuthRepository smsAuthRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    /**
     * SMS 인증번호를 전송합니다.
     *
     * @param phoneNumber   전화번호
     * @param type          SMS 타입 (SIGN_UP, RESET_PWD 등)
     */
    @Override
    public void sendSmsCode(String phoneNumber, SmsType type) {
        phoneNumber = eliminateDash(phoneNumber);
        String code = CodeGenerator.generateCode(digitLength);

        SmsAuth smsAuth = SmsAuth.builder()
                .id(UUID.randomUUID())
                .phone(phoneNumber)
                .smsType(type)
                .authCode(code)
                .smsStatus(SmsStatus.UNCHECKED)
                .expiration(LocalDateTime.now().plusSeconds(type.getExpirationSecond()))
                .lastModified(LocalDateTime.now())
                .trial(1)
                .build();

        if (type.equals(SmsType.SIGN_UP) || type.equals(SmsType.CHANGE_PHONE)) {
            Optional<User> user = userRepository.findByPhone(phoneNumber);
            if (user.isPresent()) {
                throw new AlreadyUserException();
            }
        }

        nhnSmsService.saveOrUpdate(smsAuth);
        Locale locale = LocaleContextHolder.getLocale();
        nhnSmsService.sendSms(phoneNumber, messageSource.getMessage("sms." + type.name().toLowerCase() + "-message", new Object[]{code}, locale));
    }

    private String eliminateDash(String phoneNumber) {
        return phoneNumber.replaceAll("-", "").trim();
    }

    /**
     * SMS코드가 정확한지 검사합니다. 틀릴 경우 Exception이 발생합니다.
     *
     * @param phoneNumber 전화번호
     * @param code        받은 SMS 코드
     */
    @Transactional
    @Override
    public void verifySmsCode(String phoneNumber, String code, SmsType smsType) {
        phoneNumber = eliminateDash(phoneNumber);
        Optional<SmsAuth> maybeSmsAuth = smsAuthRepository.findValidSmsAuth(phoneNumber, smsType);

        if (maybeSmsAuth.isEmpty()) {
            throw new SmsAuthNotFoundException();
        }

        SmsAuth smsAuth = maybeSmsAuth.get();

        if (!smsAuth.getAuthCode().equals(code.trim())) {
            throw new SmsCodeNotValidException();
        }

        nhnSmsService.updateToChecked(smsAuth);
    }

    @Override
    public SmsAuth getSmsAuth(String validatedPhoneNumber, SmsType type) {
        validatedPhoneNumber = eliminateDash(validatedPhoneNumber);
        Optional<SmsAuth> smsAuth = smsAuthRepository.getSmsAuth(validatedPhoneNumber, type);
        if (smsAuth.isEmpty()) {
            throw new SmsAuthNotFoundException();
        }

        SmsAuth aliveSmsAuth = smsAuth.get();
        if (aliveSmsAuth.getSmsStatus() == SmsStatus.CHECKED) {
            return aliveSmsAuth;
        }
        throw new SmsAuthNotFoundException();
    }
}
