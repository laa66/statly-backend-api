package com.laa66.statlyapp.DTO;

import lombok.*;

@Value
public class ExceptionDTO {
    int status;
    String message;
    long timestamp;
}
