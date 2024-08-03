package org.anipotbackend.global.interceptor.model.dto;

import lombok.Getter;

@Getter
public class SuccessResponse {
    private final String message;

    public SuccessResponse() {
        this("ok");
    }

    public SuccessResponse(String message) {
        this.message = message;
    }
}
