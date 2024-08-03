package org.anipotbackend.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anipotbackend.domain.user.model.dto.request.SendSmsCodeRequest;
import org.anipotbackend.domain.user.model.dto.request.UserLoginRequest;
import org.anipotbackend.domain.user.model.dto.request.UserSignupRequest;
import org.anipotbackend.domain.user.model.dto.request.VerifySmsCodeRequest;
import org.anipotbackend.domain.user.model.dto.response.LoginTokenResponse;
import org.anipotbackend.domain.user.service.SignupService;
import org.anipotbackend.domain.user.service.UserService;
import org.anipotbackend.infra.nhn.sms.service.SmsManageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 API")
public class UserController {

    private final UserService userService;
    private final SignupService signupService;

    /**
     * 회원가입
     *
     * <p>회원가입을 하기 위해서는 SMS 인증이 선행되어야 합니다.</p>
     */
    @PostMapping("/signup")
    public void signup(@Valid @RequestBody UserSignupRequest request) {
        signupService.signup(request);
    }

    /**
     * 로그인
     *
     * @param request   로그인 요청
     * @return          로그인 성공시 토큰 반환
     */
    @PostMapping("/login")
    public LoginTokenResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userService.login(request);
    }
}
