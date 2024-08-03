package org.anipotbackend.global.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.global.error.exception.*;
import org.anipotbackend.global.error.model.ErrorSource;
import org.anipotbackend.global.error.model.dto.response.ErrorResponseDto;
import org.anipotbackend.global.error.model.entity.ErrorLogEntity;
import org.anipotbackend.global.error.service.ErrorLogService;
import org.anipotbackend.global.error.service.ErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ControllerAdvisor {

    private final MessageSource messageSource;
    private final ErrorLogService errorLogService;
    private final ErrorService errorService;

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @Value("${app.enable-test-controller}")
    private boolean isEnabledTest;

    @ExceptionHandler
    @Transactional
    protected ResponseEntity<ErrorResponseDto> localizedException(HttpServletRequest request, LocalizedMessageException e, Locale locale) {
        ErrorResponseDto dto = new ErrorResponseDto(messageSource, locale, e);
        String logMessage = createLogMessage(dto.getTrackingId(), e);
        logger.error(logMessage);
        saveErrorLogEntity(request, e, dto, logMessage);
        if (containsEnum(e.getStatus())) {
            return filter(e, ResponseEntity.status(e.getStatusCode()).body(dto));
        }
        return filter(e, ResponseEntity.status(HttpStatus.valueOf(e.getStatus())).body(dto));
    }

    protected void saveErrorLogEntity(HttpServletRequest request, LocalizedMessageException e, ErrorResponseDto dto, String logMessage) {
        ErrorLogEntity entity = new ErrorLogEntity(
                LocalDateTime.now(),
                ErrorSource.SERVER,
                request.getRequestURI(),
                messageSource.getMessage(e.getLocalizedMessage(), null, LocaleContextHolder.getLocale()),
                request.getServerName(),
                dto.getStatusCode(),
                dto.getStatus(),
                dto.getCode(),
                logMessage,
                e.getLocalizedMessage(),
                (e.getCause() != null) ? e.getCause().toString() : null,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).findFirst().toString()
                        .replaceAll("Optional\\[", "").replaceAll("]", "")
        );
        checkEntityField(request, e, entity);
        errorService.saveErrorLogEntity(entity);
    }

    private String createLogMessage(String trackingId, LocalizedMessageException e) {
        return String.format("A problem has occurred in controller advice: [id=%s]\n%s", trackingId, e);
    }

    private boolean containsEnum(String constantName) {
        for (CustomHttpStatus status : CustomHttpStatus.VALUES) {
            if (status.name().equals(constantName)) {
                return true;
            }
        }
        return false;
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(HttpServletRequest request, HttpMessageNotReadableException e, Locale locale) {
        return localizedException(request, new BadRequestException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(HttpServletRequest request, BindException e, Locale locale) {
        return localizedException(request, new BindingFailedException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(HttpServletRequest request, MethodArgumentTypeMismatchException e, Locale locale) {
        return localizedException(request, new BadRequestException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(HttpServletRequest request, MissingServletRequestParameterException e, Locale locale) {
        return localizedException(request, new MissingRequiredParamterException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badMethod(HttpServletRequest request, HttpRequestMethodNotSupportedException e, Locale locale) {
        return localizedException(request, new NotSupportedMethodException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> accessDenied(HttpServletRequest request, AccessDeniedException e, Locale locale) {
        return localizedException(request, new NotGrantedException(e), locale);
    }

    @ExceptionHandler
    @Transactional
    protected ResponseEntity<ErrorResponseDto> unexpectedException(HttpServletRequest request, Exception e, Locale locale) {
        ErrorResponseDto dto = new ErrorResponseDto(messageSource, locale, LocalizedMessageException.of(e));
        String logMessage = createLogMessage(UUID.randomUUID().toString(), LocalizedMessageException.of(e));
        log.error("Unexpected exception has occurred in controller advice: [id={}]", UUID.randomUUID(), e);
        ErrorLogEntity entity = new ErrorLogEntity(
                LocalDateTime.now(),
                ErrorSource.SERVER,
                request.getRequestURI(),
                e.getLocalizedMessage(),
                request.getServerName(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                e.getClass().getSimpleName(),
                logMessage,
                e.getLocalizedMessage(),
                (e.getCause() != null) ? e.getCause().toString() : null,
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).findFirst().toString()
                        .replaceAll("Optional\\[", "").replaceAll("]", "")
        );
        errorService.saveErrorLogEntity(entity);
        return filter(e, ResponseEntity.internalServerError().body(dto));
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> handlerMethodValidationException(HttpServletRequest request, HandlerMethodValidationException e, Locale locale) {
        return localizedException(request, new BadRequestException(e), locale);
    }

    private ResponseEntity<ErrorResponseDto> filter(Throwable t, ResponseEntity<ErrorResponseDto> entity) {
        ErrorResponseDto dto = entity.getBody();
        if (isEnabledTest && dto != null) {
            errorLogService.logError(dto.getTrackingId(), t, dto);
        }
        return entity;
    }

    /**
     * ErrorLogEntity 필드 확인을 위한 log 메소드
     */
    private void checkEntityField(HttpServletRequest request, LocalizedMessageException e, ErrorLogEntity entity) {
        log.info("ErrorSource : {}", entity.getSource().toString());
        log.info("ErrorOccurPath : {}", entity.getOccurPath());
        log.info("ErrorMessage : {}", messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale()));
        log.info("ErrorHost : {}", request.getServerName());
        log.info("ErrorStatusCode : {}", entity.getStatusCode());
        log.info("ErrorStatus : {}", entity.getStatus());
        log.info("ErrorCode : {}", entity.getCode());
        log.info("ErrorErrorCode : {}", entity.getErrorCode());
        log.info("ErrorCausedBy : {}", (e.getCause() != null) ? e.getCause().toString() : null );
        log.info("ErrorServicePoint : {}", entity.getServicePoint());
    }
}
