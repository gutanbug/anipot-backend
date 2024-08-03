package org.anipotbackend.domain.user.model.dto.response;

import lombok.Getter;
import org.anipotbackend.global.auth.jwt.AuthenticationToken;

@Getter
public class LoginTokenResponse {

    private final String accessToken;
    private final String refreshToken;

    public LoginTokenResponse(AuthenticationToken token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
