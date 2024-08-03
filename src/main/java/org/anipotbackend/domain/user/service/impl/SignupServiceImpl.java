package org.anipotbackend.domain.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.domain.user.exception.AlreadyUserException;
import org.anipotbackend.domain.user.model.dto.request.UserSignupRequest;
import org.anipotbackend.domain.user.model.entity.User;
import org.anipotbackend.domain.user.repository.UserRepository;
import org.anipotbackend.domain.user.service.SignupService;
import org.anipotbackend.infra.nhn.sms.model.SmsType;
import org.anipotbackend.infra.nhn.sms.service.SmsManageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SignupServiceImpl implements SignupService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SmsManageService smsManageService;

    @Override
    @Transactional
    public void signup(UserSignupRequest request) {
        String validatePhone = eliminateDash(request.getPhone());

        checkAlreadyPhone(validatePhone);
        checkAlreadyLoginId(request.getLoginId());
        checkAlreadyEmail(request.getEmail());

        String successPhoneNumber = smsManageService.getSmsAuth(validatePhone, SmsType.SIGN_UP).getPhone();

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(encoder.encode(request.getPassword()))
                .email(request.getEmail())
                .nickname(request.getNickname())
                .phone(successPhoneNumber)
                .birthDate(eliminateDash(request.getBirthDate()))
                .build();
        userRepository.save(user);
    }

    private void checkAlreadyPhone(String validatePhone) {
        if (userRepository.findByPhone(validatePhone).isPresent()) {
            throw new AlreadyUserException();
        }
    }

    private void checkAlreadyLoginId(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new AlreadyUserException();
        }
    }

    private void checkAlreadyEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AlreadyUserException();
        }
    }

    public String eliminateDash(String phone) {
        return phone.trim().replaceAll("-", "");
    }
}
