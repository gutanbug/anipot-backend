package org.anipotbackend.domain.user.model;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BANNED,
    WITHDRAWAL;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isStatus(UserStatus status) {
        return this == status;
    }
}
