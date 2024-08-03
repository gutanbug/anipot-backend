package org.anipotbackend.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationTokenProvider {

    String getTokenFromHeader(HttpServletRequest request, String prefix);

    JwtAuthentication getAuthentication(HttpServletRequest request, HttpServletResponse response);
}
