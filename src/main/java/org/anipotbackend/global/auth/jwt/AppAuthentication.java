package org.anipotbackend.global.auth.jwt;

import org.anipotbackend.domain.user.model.UserStatus;
import org.anipotbackend.global.auth.UserRole;
import org.springframework.security.core.Authentication;

public interface AppAuthentication extends Authentication {

    String getUserSubId();

    UserRole getUserRole();

    UserStatus getUserStatus();

    boolean isAdmin();
}
