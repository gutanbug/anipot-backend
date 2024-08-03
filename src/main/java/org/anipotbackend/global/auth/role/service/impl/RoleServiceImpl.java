package org.anipotbackend.global.auth.role.service.impl;

import lombok.RequiredArgsConstructor;
import org.anipotbackend.global.auth.UserRole;
import org.anipotbackend.global.auth.jwt.JwtAuthentication;
import org.anipotbackend.global.auth.role.service.RoleService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service("RoleService")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    @Override
    public boolean matchWithRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            UserRole roleOfMine = ((JwtAuthentication) authentication).getUserRole();
            return roleOfMine.contains(role);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AccessDeniedException("invalid.role");
        }
    }
}
