package org.anipotbackend.global.auth;

public class UserAuthNames {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_GUEST = "ROLE_GUEST";
    public static final String ROLE_USER = "ROLE_USER";

    public static String combine(String... names) {
        return String.join(",", names);
    }
}
