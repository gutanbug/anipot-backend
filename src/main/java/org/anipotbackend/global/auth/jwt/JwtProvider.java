package org.anipotbackend.global.auth.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.domain.user.model.UserStatus;
import org.anipotbackend.domain.user.model.entity.User;
import org.anipotbackend.global.auth.UserRole;
import org.anipotbackend.global.error.exception.IllegalTokenTypeException;
import org.anipotbackend.global.error.exception.InvalidJwtTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider implements AuthenticationTokenProvider {

    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCESS_PREFIX = "anipot-access";
    public static final String REFRESH_PREFIX = "anipot-refresh";

    @Value("${app.auth.jwt.access-expiration}")
    private Duration accessExpiration;

    @Value("${app.auth.jwt.refresh-expiration}")
    private Duration refreshExpiration;

    @Value("${app.auth.jwt.secret-key}")
    private String secretKey;


    @Override
    public String getTokenFromHeader(HttpServletRequest request, String prefix) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        Optional<Cookie> maybeCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equalsIgnoreCase(prefix))
                .findFirst();

        if (maybeCookie.isPresent()) {
            return maybeCookie.get().getValue();
        }

        String header = request.getHeader(AUTHORIZATION);
        if (header != null) {
            if (!header.toLowerCase().startsWith("bearer ")) {
                throw new IllegalTokenTypeException();
            }
            return header.substring(7);
        }
        return null;
    }

    @Override
    public JwtAuthentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getTokenFromHeader(request, ACCESS_PREFIX);
        String refreshToken = getTokenFromHeader(request, REFRESH_PREFIX);

        Jws<Claims> claimsJws = null;
        try {
            claimsJws = validateToken(accessToken);
        } catch (ExpiredJwtException e) {
            AuthenticationToken authenticationToken = reissue(accessToken, refreshToken);
            claimsJws = validateToken(authenticationToken.getAccessToken());
        }
        if (claimsJws == null) throw new InvalidJwtTokenException();

        Claims body = claimsJws.getBody();
        String userSubId = (String) body.get("userSubId");
        UserRole userRole = UserRole.valueOf((String) body.get("userRole"));
        UserStatus userStatus = UserStatus.valueOf((String) body.get("userStatus"));

        return new JwtAuthentication(userSubId, userRole, userStatus);
    }

    private Jws<Claims> validateToken(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            log.error("expired jwt exception by " + accessToken);
            throw e;
        } catch (JwtException e) {
            log.error("invalid token by " + accessToken);
            throw new InvalidJwtTokenException();
        }
    }

    public JwtAuthenticationToken issue(User user) {
        return JwtAuthenticationToken.builder()
                .accessToken(createAccessToken(user.getSubId().toString(), user.getUserRole(), user.getUserStatus()))
                .refreshToken(createRefreshToken())
                .build();
    }

    public JwtAuthenticationToken reissue(String accessToken, String refreshToken) {
        try {
            validateToken(refreshToken);

            return JwtAuthenticationToken.builder()
                    .accessToken(refreshAccessToken(accessToken))
                    .refreshToken(refreshToken)
                    .build();
        } catch (ExpiredJwtException e) {
            return JwtAuthenticationToken.builder()
                    .accessToken(refreshAccessToken(accessToken))
                    .refreshToken(createRefreshToken())
                    .build();
        }
    }

    private String refreshAccessToken(String accessToken) {
        String userId;
        UserRole role;
        UserStatus userStatus;
        try {
            Jws<Claims> claimsJws = validateToken(accessToken);
            Claims body = claimsJws.getBody();
            userId = (String) body.get("userId");
            role = UserRole.of((String) body.get("userRole"));
            userStatus = UserStatus.valueOf((String) body.get("userStatus"));
        } catch (ExpiredJwtException e) {
            userId = (String) e.getClaims().get("userId");
            role = UserRole.of((String) e.getClaims().get("userRole"));
            userStatus = UserStatus.valueOf((String) e.getClaims( ).get("userStatus"));
        }
        return createAccessToken(userId, role, userStatus);
    }

    private String createAccessToken(String userSubId, UserRole role, UserStatus status) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(accessExpiration);

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("userSubId", userSubId);
        payloads.put("userRole", role);
        payloads.put("userStatus", status);

        return Jwts.builder()
                .setSubject("UserInfo")
                .setClaims(payloads)
                .setIssuedAt(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    private String createRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(refreshExpiration);

        return Jwts.builder()
                .setIssuedAt(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }
}
