package org.anipotbackend.infra.nhn.sms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsType {
    @Schema(description = "회원 가입시에 전송됨")
    SIGN_UP(300),

    @Schema(description = "아이디 찾기 시에 전송됨")
    FIND_ID(300),

    @Schema(description = "비밀번호 재설정 시에 전송됨")
    RESET_PWD(300),

    @Schema(description = "비밀번호 변경 시에 전송됨")
    CHANGE_PWD(300),

    @Schema(description = "휴대폰 번호 변경 시에 전송됨")
    CHANGE_PHONE(300);

    final private int expirationSecond;
}
