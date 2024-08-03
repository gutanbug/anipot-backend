package org.anipotbackend.domain.user.service;

import org.anipotbackend.domain.user.model.dto.request.UserSignupRequest;

public interface SignupService {
    void signup(UserSignupRequest request);
    void checkValidPhone(String phone);
    void checkValidEmail(String email);
    void checkAlreadyPhone(String phone);
    void checkAlreadyLoginId(String loginId);
    void checkAlreadyEmail(String email);
    void checkValidNickname(String nickname);
    void checkValidPassword(String password);
}
