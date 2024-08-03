package org.anipotbackend.global.auth.jwt;

import org.anipotbackend.domain.user.model.UserStatus;
import org.anipotbackend.global.auth.UserRole;

public class GuestAuthentication extends JwtAuthentication{

    public GuestAuthentication(String userSubId, UserRole userRole, UserStatus userStatus) {
        super(userSubId, userRole, userStatus);
    }

    public static GuestAuthentication getAuthentication( ) {
        return new GuestAuthentication(null, UserRole.GUEST, UserStatus.ACTIVE);
    }
}
