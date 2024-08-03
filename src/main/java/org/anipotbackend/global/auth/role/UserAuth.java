package org.anipotbackend.global.auth.role;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.anipotbackend.global.auth.jwt.JwtProvider;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = JwtProvider.AUTHORIZATION)
@PreAuthorize("@RoleService.matchWithRole('ROLE_USER')")
public @interface UserAuth {
}
