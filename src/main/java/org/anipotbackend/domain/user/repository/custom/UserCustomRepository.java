package org.anipotbackend.domain.user.repository.custom;

import io.lettuce.core.dynamic.annotation.Param;
import org.anipotbackend.domain.user.model.entity.User;

import java.util.Optional;

public interface UserCustomRepository {
    Optional<User> findByPhone(@Param("validatePhone") String validatePhone);

    Optional<User> findByLoginId(@Param("loginId") String loginId);

    Optional<User> findByEmail(@Param("email") String email);
}
