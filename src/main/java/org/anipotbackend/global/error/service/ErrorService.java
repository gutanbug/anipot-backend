package org.anipotbackend.global.error.service;

import org.anipotbackend.global.auth.jwt.AppAuthentication;
import org.anipotbackend.global.base.ResponsePage;
import org.anipotbackend.global.error.model.ErrorSource;
import org.anipotbackend.global.error.model.dto.response.ResponseErrorInfo;
import org.anipotbackend.global.error.model.entity.ErrorLogEntity;
import org.springframework.data.domain.Pageable;

public interface ErrorService {
    ResponsePage<ResponseErrorInfo> getAllErrorLog(ErrorSource source, Pageable pageable, AppAuthentication auth);

    void saveErrorLogEntity(ErrorLogEntity entity);
}
