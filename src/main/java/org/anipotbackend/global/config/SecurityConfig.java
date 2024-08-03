package org.anipotbackend.global.config;

import lombok.RequiredArgsConstructor;
import org.anipotbackend.global.auth.JwtAuthenticationFilter;
import org.anipotbackend.global.error.CustomAccessDeniedHandler;
import org.anipotbackend.global.error.CustomAuthenticationEntryPoint;
import org.anipotbackend.global.error.ExceptionalHandlerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionalHandlerFilter exceptionalHandlerFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private static final String[] PUBLIC_URI = {
            "/swagger-ui/**", "/api-docs/**", "/test/**", "/**"
    };

    private static final String[] ADMIN_URI = {
            "/admin/**", "/manage/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers(PUBLIC_URI).permitAll()
                                .requestMatchers(ADMIN_URI).hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionalHandlerFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(
                        handling ->
                                handling.accessDeniedHandler(customAccessDeniedHandler)
                                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
