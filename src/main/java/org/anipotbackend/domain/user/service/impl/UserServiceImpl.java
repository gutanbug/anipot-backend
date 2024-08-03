package org.anipotbackend.domain.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.domain.user.exception.LoginFailedException;
import org.anipotbackend.domain.user.model.dto.request.UserLoginRequest;
import org.anipotbackend.domain.user.model.dto.response.LoginTokenResponse;
import org.anipotbackend.domain.user.model.entity.User;
import org.anipotbackend.domain.user.repository.UserRepository;
import org.anipotbackend.domain.user.service.UserService;
import org.anipotbackend.global.auth.jwt.AuthenticationToken;
import org.anipotbackend.global.auth.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public LoginTokenResponse login(UserLoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(LoginFailedException::new);

        if (encoder.matches(request.getPassword(), user.getPassword())) {
            AuthenticationToken token = jwtProvider.issue(user);
            return new LoginTokenResponse(token);
        } else {
            throw new LoginFailedException();
        }
    }
}
