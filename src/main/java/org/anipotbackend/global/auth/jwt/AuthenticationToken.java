package org.anipotbackend.global.auth;

public interface AuthenticationToken {
    String getAccessToken();

    String getRefreshToken();
}
