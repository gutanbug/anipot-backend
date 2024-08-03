package org.anipotbackend.global.swagger.auth;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.anipotbackend.global.swagger.auth.UserAuthNames.*;

@Getter
public enum UserRole {
    USER(ROLE_USER),
    GUEST(ROLE_GUEST),
    ADMIN(combine(ROLE_ADMIN, ROLE_USER, ROLE_GUEST));

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    private static final Map<String, UserRole> BY_LABEL =
            Stream.of(values()).collect(Collectors.toMap(UserRole::getName, e -> e));

    public static UserRole of(String name) {
        return BY_LABEL.get(name);
    }

    public boolean contains(String role) {
        return Arrays.stream(this.getName().split(","))
                .anyMatch(avail -> avail.equals(role));
    }

    public static UserRole from(ApopAuthentication auth) {
        if (auth == null) {
            return GUEST;
        }
        return auth.getUserRole();
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isUser( ) {
        return this == USER;
    }
}
