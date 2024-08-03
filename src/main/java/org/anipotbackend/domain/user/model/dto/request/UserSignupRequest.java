package org.anipotbackend.domain.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignupRequest {
    @NotNull
    @Schema(description = "로그인 아이디", example = "loginid")
    private String loginId;
    @NotNull
    @Schema(description = "비밀번호", example = "password")
    private String password;
    @NotNull
    @Schema(description = "이메일", example = "example@gmail.com")
    private String email;
    @NotNull
    @Schema(description = "닉네임", example = "닉네임")
    private String nickname;
    @NotNull
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @NotNull
    @Schema(description = "생년월일", example = "1990-01-01")
    private String birthDate;
}
