package org.anipotbackend.infra.nhn.sms.model.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.anipotbackend.infra.nhn.sms.model.SmsStatus;
import org.anipotbackend.infra.nhn.sms.model.SmsType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "sms_code")
@Getter
@AllArgsConstructor
@Builder
public class SmsAuth {

    @Id
    @Field(name = "id")
    private UUID id;

    @Field(name = "phone")
    private String phone;

    @Field(name = "situation_code")
    private SmsType smsType;

    @Field(name = "code")
    private String authCode;

    @Field(name = "status")
    private SmsStatus smsStatus;

    @Field(name = "expiration")
    private LocalDateTime expiration;

    @Field(name = "last_modified")
    private LocalDateTime lastModified;

    @Field(name = "trial")
    private int trial;

    public void changeStatus(SmsStatus smsStatus) {
        this.smsStatus = smsStatus;
    }

    public boolean isExpired( ) {
        return this.expiration.isBefore(LocalDateTime.now());
    }

    public boolean isAlive( ) {
        return this.expiration.isAfter(LocalDateTime.now());
    }

    public void clearTrial() {
        this.trial = 0;
    }
}
