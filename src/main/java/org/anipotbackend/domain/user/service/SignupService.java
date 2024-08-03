package org.anipotbackend.domain.user.service;

import org.anipotbackend.domain.user.model.dto.request.UserSignupRequest;

public interface SignupService {
    void signup(UserSignupRequest request);
}
