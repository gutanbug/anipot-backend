package org.anipotbackend.global.error.model.dto.response;

import lombok.Getter;
import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Getter
public class ErrorResponseDto {
    private final String timestamp;
    private final String trackingId;
    private final int statusCode;
    private final String status;
    private final String code;
    private final List<Object> message;

    public ErrorResponseDto(MessageSource messageSource, Locale locale, LocalizedMessageException e) {
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.statusCode = e.getStatusCode();
        this.status = e.getStatus();
        this.code = e.getCode();
        this.message = e.getMessages(messageSource, locale);
    }
}
