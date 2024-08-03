package org.anipotbackend.global.error.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.global.auth.jwt.AppAuthentication;
import org.anipotbackend.global.base.ResponsePage;
import org.anipotbackend.global.error.model.ErrorSource;
import org.anipotbackend.global.error.model.dto.response.ResponseErrorInfo;
import org.anipotbackend.global.error.model.entity.ErrorLogEntity;
import org.anipotbackend.global.error.repository.ErrorRepository;
import org.anipotbackend.global.error.service.ErrorService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorServiceImpl implements ErrorService {

    private final ErrorRepository errorRepository;

    @Override
    public ResponsePage<ResponseErrorInfo> getAllErrorLog(ErrorSource source, Pageable pageable, AppAuthentication auth) {
        return null;
    }

    @Override
    public void saveErrorLogEntity(ErrorLogEntity entity) {
        errorRepository.save(entity);
    }
}
