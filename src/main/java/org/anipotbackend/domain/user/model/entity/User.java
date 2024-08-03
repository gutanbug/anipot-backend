package org.anipotbackend.domain.user.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anipotbackend.domain.user.model.UserStatus;
import org.anipotbackend.global.auth.UserRole;
import org.anipotbackend.global.base.BaseEntity;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private String loginId;

    private String password;

    private String email;

    private String nickname;

    private String phone;

    private String birthDate;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Builder
    private User(@NotNull String loginId,
                @NotNull String password,
                @NotNull String email,
                @NotNull String nickname,
                @NotNull String phone,
                @NotNull String birthDate) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.userRole = UserRole.USER;
        this.userStatus = UserStatus.ACTIVE;
        this.birthDate = birthDate;
    }

    public void changeStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
