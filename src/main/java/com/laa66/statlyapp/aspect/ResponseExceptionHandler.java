package com.laa66.statlyapp.aspect;

import com.laa66.statlyapp.exception.*;
import com.laa66.statlyapp.DTO.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({TooManyRequestsException.class})
    public ResponseEntity<ExceptionDTO> handleTooManyRequestsException(TooManyRequestsException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.TOO_MANY_REQUESTS.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exceptionDTO);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ExceptionDTO> handleBadRequestException(BadRequestException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDTO);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ExceptionDTO> handleUserNotFoundException(UserNotFoundException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler({UserAuthenticationException.class})
    public ResponseEntity<ExceptionDTO> handleAuthenticationException(UserAuthenticationException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionDTO);
    }

    @ExceptionHandler({ClientAuthorizationException.class})
    public ResponseEntity<ExceptionDTO> handleClientAuthorizationException(ClientAuthorizationException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.FORBIDDEN.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDTO);
    }

    @ExceptionHandler({RestClientException.class})
    public ResponseEntity<ExceptionDTO> handleRestClientException(RestClientException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exceptionDTO);
    }

    @ExceptionHandler({SpotifyAPIEmptyResponseException.class})
    public ResponseEntity<ExceptionDTO> handleSpotifyAPIEmptyResponseException(SpotifyAPIEmptyResponseException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.NO_CONTENT.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(exceptionDTO);
    }

    @ExceptionHandler({SpotifyAPIException.class})
    public ResponseEntity<ExceptionDTO> handleClientAuthorizationException(SpotifyAPIException e) {
        log.error("Error: " + e.getMessage(), e);
        int code = e.getCode();
        ExceptionDTO exceptionDTO = new ExceptionDTO(code, e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.valueOf(code)).body(exceptionDTO);
    }

    @ExceptionHandler({MailException.class})
    public ResponseEntity<ExceptionDTO> handleMailException(MailException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exceptionDTO);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ExceptionDTO> handleRuntimeException(RuntimeException e) {
        log.error("Error: " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDTO);
    }

}
