package org.anipotbackend.domain.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class UserLoginRequest {

    @Schema(description = "로그인 아이디", example = "anipot")
    private String loginId;
    @Schema(description = "비밀번호", example = "1234")
    private String password;
}
