package org.anipotbackend.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anipotbackend.global.error.exception.LocalizedMessageException;
import org.anipotbackend.global.error.model.dto.response.ErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionalHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final static int UNEXPECTED_STATUS_CODE = 500;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (LocalizedMessageException e) {
            writeErrorResponse(response, request.getLocale(), e);
        } catch (Exception e) {
            writeUnexpectedErrorResponse(response, request.getLocale(), e);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, Locale locale, LocalizedMessageException e) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(messageSource, locale, e);
        log.error("A problem has occurred in filter : [id={}]", errorResponse.getTrackingId());
        writeResponse(response, errorResponse, e.getStatusCode());
    }

    private void writeUnexpectedErrorResponse(HttpServletResponse response, Locale locale, Exception e) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(messageSource, locale, LocalizedMessageException.of(e));
        log.error("Unexpected exception has occurred in filter: [id={}]", errorResponse.getTrackingId(), e);
        writeResponse(response, errorResponse, UNEXPECTED_STATUS_CODE);
    }

    private void writeResponse(HttpServletResponse response, Object errorResponse, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String exceptionMessage = objectMapper.writeValueAsString(errorResponse);
        log.error(exceptionMessage);
    }
}
