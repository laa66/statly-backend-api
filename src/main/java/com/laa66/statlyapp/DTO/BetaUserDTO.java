package com.laa66.statlyapp.DTO;

import lombok.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BetaUserDTO {
    String fullName;
    String email;
    String date;
}
