package org.anipotbackend.global.error.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.anipotbackend.global.error.model.entity.ErrorLogEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class ResponseErrorInfo {
    @Schema(description = "에러 발생 환경", example = "SERVER")
    private final String source;
    @Schema(description = "에러 로그 Id", example = "1")
    private final Long id;
    @Schema(description = "에러 발생 시간", example = "2024.07.21 10:16:27.51 GMT+09:00")
    private final String time;
    @Schema(description = "에러 발생 경로", example = "/test/log")
    private final String path;
    @Schema(description = "에러 메시지", example = "에러 로그가 존재하지 않습니다.")
    private final String message;
    @Schema(description = "에러가 발생한 도메인", example = "localhost")
    private final String host;
    @Schema(description = "트래킹 Id", example = "509840914 | c74b07a6-890e-4aff-g034-5232e4810f9d")
    private final String trackingId;

    public ResponseErrorInfo(ErrorLogEntity errorLog, String trackingId) {
        this.source = errorLog.getSource().toString();
        this.id = errorLog.getId();
        this.time = changeFormat(errorLog.getCreatedAt());
        this.path = errorLog.getOccurPath();
        this.message = errorLog.getMessage();
        this.host = errorLog.getHost();
        this.trackingId = trackingId;
    }

    private String changeFormat(LocalDateTime createdAt) {
        ZonedDateTime zonedDateTime = createdAt.atZone(ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SS 'GMT'XXX")
                .format(zonedDateTime);
    }
}
