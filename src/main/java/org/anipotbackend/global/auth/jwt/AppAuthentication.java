package org.anipotbackend.global.auth;

import org.anipotbackend.domain.user.model.UserStatus;
import org.springframework.security.core.Authentication;

public interface AppAuthentication extends Authentication {

    String getUserSubId();

    UserRole getUserRole();

    UserStatus getUserStatus();

    boolean isAdmin();
}
