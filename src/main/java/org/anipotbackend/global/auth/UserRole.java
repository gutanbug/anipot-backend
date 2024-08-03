package org.anipotbackend.global.auth;

import lombok.Getter;
import org.anipotbackend.global.auth.jwt.AppAuthentication;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum UserRole {
    USER(UserAuthNames.ROLE_USER),
    GUEST(UserAuthNames.ROLE_GUEST),
    ADMIN(UserAuthNames.combine(UserAuthNames.ROLE_ADMIN, UserAuthNames.ROLE_USER, UserAuthNames.ROLE_GUEST));

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

    public static UserRole from(AppAuthentication auth) {
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
