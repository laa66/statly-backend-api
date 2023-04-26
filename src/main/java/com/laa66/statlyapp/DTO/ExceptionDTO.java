package com.laa66.statlyapp.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ExceptionDTO {

    private int status;
    private String message;
    private long timestamp;
}
