package com.laa66.statlyapp.aspect;

import com.laa66.statlyapp.exception.ClientAuthorizationException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.DTO.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserAuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionDTO> handleAuthenticationException(UserAuthenticationException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionDTO);
    }

    @ExceptionHandler({ClientAuthorizationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ExceptionDTO> handleClientAuthorizationException(ClientAuthorizationException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.FORBIDDEN.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDTO);
    }

    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public ResponseEntity<ExceptionDTO> handleHttpClientAndServerErrorException(HttpStatusCodeException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(e.getStatusCode().value(), e.getStatusText(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode().value())).body(exceptionDTO);
    }

    @ExceptionHandler({SpotifyAPIException.class})
    public ResponseEntity<ExceptionDTO> handleClientAuthorizationException(SpotifyAPIException e) {
        int code = e.getCode();
        ExceptionDTO exceptionDTO = new ExceptionDTO(code, e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.valueOf(code)).body(exceptionDTO);
    }

    @ExceptionHandler({MailException.class})
    public ResponseEntity<ExceptionDTO> handleMailException(MailException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage(), System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exceptionDTO);
    }

}
