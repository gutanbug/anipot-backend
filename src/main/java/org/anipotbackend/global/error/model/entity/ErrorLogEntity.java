package org.anipotbackend.global.error.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anipotbackend.global.base.BaseEntity;
import org.anipotbackend.global.error.model.ErrorSource;

import java.time.LocalDateTime;

@Getter
@Table(name = "error_log")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorLogEntity extends BaseEntity {

    private LocalDateTime occuredAt;

    @Enumerated(EnumType.STRING)
    private ErrorSource source;

    private String occurPath;

    @Lob
    private String message;

    private String host;

    private int statusCode;

    private String status;

    private String code;

    @Lob
    private String errorPoint;

    @Lob
    private String errorCode;

    @Lob
    private String causedBy;

    @Lob
    private String servicePoint;

    public ErrorLogEntity(LocalDateTime occuredAt,
                          ErrorSource source,
                          String occurPath,
                          String message,
                          String host,
                          int statusCode,
                          String status,
                          String code,
                          String errorPoint,
                          String errorCode,
                          String causedBy,
                          String servicePoint) {
        this.occuredAt = occuredAt;
        this.source = source;
        this.occurPath = occurPath;
        this.message = message;
        this.host = host;
        this.statusCode = statusCode;
        this.status = status;
        this.code = code;
        this.errorPoint = errorPoint;
        this.errorCode = errorCode;
        this.causedBy = causedBy;
        this.servicePoint = servicePoint;
    }


}
