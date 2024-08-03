package org.anipotbackend.domain.user.service;

import org.anipotbackend.domain.user.model.dto.request.UserLoginRequest;
import org.anipotbackend.domain.user.model.dto.request.UserSignupRequest;
import org.anipotbackend.domain.user.model.dto.response.LoginTokenResponse;

public interface UserService {
    LoginTokenResponse login(UserLoginRequest request);
}
