package org.anipotbackend.global.auth.jwt;

import lombok.AllArgsConstructor;
import org.anipotbackend.domain.user.model.UserStatus;
import org.anipotbackend.global.auth.UserRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
public class JwtAuthentication implements AppAuthentication{

    private String userSubId;
    private UserRole userRole;
    private UserStatus userStatus;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String authority : userRole.getName().split(",")) {
            authorities.add(() -> authority);
        }
        return authorities;
    }

    @Override
    public String getUserSubId() {
        return userSubId;
    }

    @Override
    public UserRole getUserRole() {
        return userRole;
    }

    @Override
    public UserStatus getUserStatus() {
        return userStatus;
    }

    @Override
    public boolean isAdmin() {
        return userRole.isAdmin();
    }

    @Override
    public Object getCredentials() {
        return userSubId;
    }

    @Override
    public Object getDetails() {
        return userSubId;
    }

    @Override
    public Object getPrincipal() {
        return userSubId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return null;
    }
}
