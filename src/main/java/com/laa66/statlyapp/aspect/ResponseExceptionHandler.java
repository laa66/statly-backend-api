package com.laa66.statlyapp.aspect;

import com.laa66.statlyapp.exception.ClientAuthorizationException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.DTO.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserAuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ExceptionDTO> handleAuthenticationException(UserAuthenticationException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ClientAuthorizationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ExceptionDTO> handleClientAuthorizationException(ClientAuthorizationException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(HttpStatus.FORBIDDEN.value(), e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({SpotifyAPIException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleClientAuthorizationException(SpotifyAPIException e) {
        int code = e.getCode();
        ExceptionDTO exceptionDTO = new ExceptionDTO(code, e.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.valueOf(code));
    }

}
