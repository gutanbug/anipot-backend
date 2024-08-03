package org.anipotbackend.domain.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSignupRequest {
    @Schema(description = "로그인 아이디", example = "loginid")
    private String loginId;
    @Schema(description = "비밀번호", example = "password")
    private String password;
    @Schema(description = "이메일", example = "example@gmail.com")
    private String email;
    @Schema(description = "닉네임", example = "닉네임")
    private String nickname;
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @Schema(description = "생년월일", example = "1990-01-01")
    private String birthDate;
}
