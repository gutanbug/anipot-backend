package org.anipotbackend.infra.nhn.sms.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anipotbackend.domain.user.model.dto.request.SendSmsCodeRequest;
import org.anipotbackend.domain.user.model.dto.request.VerifySmsCodeRequest;
import org.anipotbackend.infra.nhn.sms.service.SmsManageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@Tag(name = "SMS", description = "SMS 관련 API")
public class SmsController {

    private final SmsManageService smsManageService;

    /**
     * SMS 인증 코드 전송
     *
     * <p>SMS 타입 : SIGN_UP(회원가입) , FIND_ID(아이디 찾기), RESET_PWD(비밀번호 재설정), CHANGE_PWD(비밀번호 변경), CHANGE_PHONE(전화번호 변경)</p>
     * <p>SMS 인증 문자를 전송합니다.</p>
     */
    @PostMapping("/code")
    public void sendSmsCode(@Valid @RequestBody SendSmsCodeRequest request) {
        smsManageService.sendSmsCode(request.getPhone(), request.getSmsType());
    }

    /**
     * SMS 인증 코드 확인
     *
     * <p>SMS 타입 : SIGN_UP(회원가입) , FIND_ID(아이디 찾기), RESET_PWD(비밀번호 재설정), CHANGE_PWD(비밀번호 변경), CHANGE_PHONE(전화번호 변경)</p>
     */
    @PostMapping("/verify")
    public void verifySmsCode(@Valid @RequestBody VerifySmsCodeRequest request) {
        smsManageService.verifySmsCode(request.getPhone(), request.getCode(), request.getSmsType());
    }
}
