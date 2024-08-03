package org.anipotbackend.domain.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.anipotbackend.infra.nhn.sms.model.SmsType;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class VerifySmsCodeRequest {

    @Schema(description = "인증 코드", example = "123456")
    private String code;
    @Schema(description = "휴대폰 번호. 대시(-)는 있어도 되고 없어도 된다.", example = "010-1111-2222")
    private String phone;
    @Schema(description = "SMS 타입", example = "SIGN_UP")
    private SmsType smsType;

}
