package org.anipotbackend.global.error.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anipotbackend.global.error.model.dto.response.ErrorResponseDto;

@Getter
@RequiredArgsConstructor
public class ErrorLog {
    public final ErrorResponseDto info;
    public final String errorLog;

    @Override
    public String toString() {
        return String.format("Tracking ID : $s\n" +
                "Timestamp : %s\n" +
                "Status : %s\n" +
                "Message : %s\n" +
                "%s",
                info.getTrackingId(), info.getTimestamp(), info.getStatus(),
                info.getCode(), info.getMessage(), errorLog);
    }
}
