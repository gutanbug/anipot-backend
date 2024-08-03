package org.anipotbackend.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anipotbackend.domain.user.model.dto.request.UserSignupRequest;
import org.anipotbackend.domain.user.service.SignupService;
import org.anipotbackend.domain.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

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
}
