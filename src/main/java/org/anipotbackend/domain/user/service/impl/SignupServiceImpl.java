package org.anipotbackend.domain.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.domain.user.exception.*;
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
        checkValidEmail(request.getEmail());
        checkValidPhone(request.getPhone());
        checkValidNickname(request.getNickname());
        checkValidPassword(request.getPassword());

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

    public void checkValidPhone(String phone) {
        if (phone.contains("-")) {
            if (phone.split("-").length != 3) {
                throw new InvalidPhoneException();
            }

            String[] splitPhone = phone.split("-");
            if (splitPhone[0].length() != 3 || splitPhone[1].length() != 4 || splitPhone[2].length() != 4) {
                throw new InvalidPhoneException();
            }
        } else {
            if (phone.length() != 11) {
                throw new InvalidPhoneException();
            }
        }
    }

    public void checkValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,8}$";
        if (!email.matches(regex)) {
            throw new InvalidEmailException();
        }
    }

    public String eliminateDash(String phone) {
        return phone.trim().replaceAll("-", "");
    }

    public void checkAlreadyPhone(String validatePhone) {
        if (userRepository.findByPhone(validatePhone).isPresent()) {
            throw new AlreadyUserException();
        }
    }

    public void checkAlreadyLoginId(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new AlreadyUserException();
        }
    }

    public void checkAlreadyEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AlreadyUserException();
        }
    }

    public void checkValidNickname(String nickname) {
        if (nickname.length() > 8) {
            throw new InvalidNicknameException();
        }
    }

    public void checkValidPassword(String password) {
        if (password.length() < 8) {
            throw new InvalidPasswordException();
        }

        String regex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+<>?{}~|]).{8,}$";

        if (!password.matches(regex)) {
            throw new InvalidPasswordException();
        }

    }
}
